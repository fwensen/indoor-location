package cn.edu.uestc.indoorlocation.algorithm.particle_filter;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Jama.Matrix;
import cn.edu.uestc.indoorlocation.algorithm.Filter;
import cn.edu.uestc.indoorlocation.algorithm.common.CommonUtils;
import cn.edu.uestc.indoorlocation.algorithm.common.Constant;
import cn.edu.uestc.indoorlocation.algorithm.common.LoadWallInfo;
import cn.edu.uestc.indoorlocation.algorithm.common.Sensors;
import cn.edu.uestc.indoorlocation.algorithm.common.WallInfo;
import cn.edu.uestc.indoorlocation.dao.model.Point;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeySizeException;
/**
 * 粒子滤波实现
 * @author wensen
 */
public class PF implements Filter{

	private static Logger LOGGER = LoggerFactory.getLogger(PF.class);
	/**
	 * 重采样的阈值
	 */
	private final int MIN_NEFF;
	private final int particleNumbers; //粒子数目
	private volatile Particle[] particles;
	private Random RANDOM = new Random();
	
	public PF(int n) {
		this.particleNumbers = n;
		MIN_NEFF = 2*n/3;
		particles = new Particle[particleNumbers];
		LOGGER.debug("particle numbers: {}", particleNumbers);
	}
	
	private Point prevPoint = null;
//	private int prevStepLength = Constant.NORMAL_STEP_LENGTH;
	
	@Override
	public void init(Point point) {
		
		if (point == null) {
			LOGGER.error("PARTICLE: init point is null");
		}
		int error = Constant.ERROR_FOR_NN;
		if (prevPoint != null) {
			
			prevPoint = new Point((int)(point.getX()*0.8 + prevPoint.getX()*0.2), 
								  (int)(point.getY()*0.8 + prevPoint.getY()*0.2));
		} else {
			prevPoint = (Point)point.clone();
		}
		
		for (int i = 0; i < particleNumbers; i++) {
			/**
			 * 生成(prevPoint.getX()-error)到(prevPoint.getX()+error)之间的随机x坐标
			 * 下面的y同
			 */
			int x = CommonUtils.generateRandomIntegerRangeNumber(prevPoint.getX(), error);
			x = x < 0 ? 1 : x;
			int y = CommonUtils.generateRandomIntegerRangeNumber(prevPoint.getY(), error);
			y = y < 0 ? 1 : y;
			//int z
//			double orientation = CommonUtils.generateRandomAngle();
			particles[i] = new Particle(x, y);
			particles[i].weight(1.0/this.particleNumbers);
			particles[i].stepLength(Constant.NORMAL_STEP_LENGTH + 
					CommonUtils.generateRandomIntegerRangeNumber(0, 7));
		}
	}
	
	/**
	 * * 预测阶段
	 * 
	 * 方位角中：正北方为0度，
	 * 		   正东方90度，
	 * 		  正南方180度(-180度)
	 * 
	 * 1:初步假设手机水平向上手持平
	 * 2:
	 * 
	 * 在实验室环境中，北南方位x轴，东西方位为y轴，其中正北方为x轴正方向，正东方为y轴正方向
	 * 
	 * (0,0)							北 x
	 * **********************************
	 * *				*				*
	 * *				*				*
	 * *	119			*		121		*
	 * *				*				*
	 * *				*				*
	 * *				*				*
	 * ** **************************** **********************
	 * *													*
	 * ***********************************************		*
	 * *											 *		*
	 * *东 y											 *		*
	 * 
	 * 目前仅使用航向角作为方向判断
	 * 为什么不用陀悬仪呢？觉得若是使用陀悬仪，可能会有迭代的误差
	 * 航向角就可以得到方向，何乐而不为呢
	 * 航向角毕竟是由客户端的传感器收集，所以我设置了10度的误差
	 * 同时步长的估计中，我设置了+/-5cm的误差，这些误差都当做噪声
	 */
	@Override
	public void predict(Sensors sensor, double al) {
		
		double alpha = Constant.STEP_LENGTH_ALPHA;
		/**
		 * 取得航向角
		 */
		double theta = CommonUtils.azimuthAngleFromSensor(sensor);
		
		//更新粒子
		for (int i = 0; i < this.particleNumbers; i++) {
			/**后面的CommonUtils.generateRandomIntegerNumber(10, 0)为噪声
			*	/////////角度偏移大概或许有2度左右的偏移
			**/
			double thta = theta;// + Math.toRadians(
//					CommonUtils.generateRandomIntegerRangeNumber(0, 3));
			
			double costheta = Math.cos(thta);
			double sintheta = Math.sin(thta);
			/**
			 * 步长估计，根据与上一步的关系计算得到
			 * 步长也有+-4 cm的噪声
			 */
			double lk = (1 - alpha)*particles[i].stepLength() + alpha*CommonUtils.lengthEstimate(sensor) + 
					      											  CommonUtils.generateRandomIntegerRangeNumber(0, 4);
			int prevx = particles[i].positionX();
			int prevy = particles[i].positionY();
			int nextx = (int)(prevx + lk*costheta);
			int nexty = (int)(prevy + lk*sintheta);
			
			/**
			 * 如果穿墙，则放弃更新该粒子
			 */
			if (isCrossWall(prevx, prevy, nextx, nexty)) {
				continue;
			}
			
			particles[i].setPositionX(nextx);
			particles[i].setPositionY(nexty);
			particles[i].stepLength((int)(lk));
//			System.out.println("x, y, length: " + nextx + ":" + nexty + ": " + lk);
		}
	}
	
	/**
	 * 状态更新
	 * 目前的权重计算仅根据欧拉距离结合高斯函数计算得到
	 */
	@Override
	public Point update(Point fingerRet, Matrix R) {
		
		//TODO
		double weightSum = 0.0;
		/**
		 * 这里考虑了指纹定位结果的误差
		 * 得到指纹定位结果中x, y坐标的误差
		 */
		int errorX = (int)R.get(0, 0);
		int errorY = (int)R.get(1, 1);
		errorX = errorX > 0 ? errorX : 1;
		errorY = errorY > 0 ? errorY : 1;
		int x = fingerRet.getX() + CommonUtils.generateRandomIntegerNumber(errorX);
		int y = fingerRet.getY() + CommonUtils.generateRandomIntegerNumber(errorX);
		
		for (Particle particle : particles) {
			/**
			 * 得到与测试点（指纹定位结果）相对距离
			 * 然后根据该距离计算高斯权重
			 */
			double dist = Math.sqrt(Math.pow(x - particle.positionX(), 2.0) 
					              + Math.pow(y - particle.positionY(), 2.0)); 
			double weight = CommonUtils.gaussian(dist/10, 10);
//			LOGGER.debug("dist/10: {}, weight: {}", dist/10, weight);
			particle.weight(weight);
			weightSum += weight;
		}
		//归一化
		for (Particle particle : particles) {
			particle.weight(particle.weight()/weightSum);
		}
		/**
		 * 是否需要重采样
		 */
		if (isNeedResample(MIN_NEFF)) {
			simpleResample();
		}
		return estimate();
	}
	
	/**
	 * 判断粒子是否穿墙
	 * 1、使用KD树和map找到离prev点最近的四点的墙体信息
	 * 2、判断prev点到next点的线段是否穿墙
	 * 
	 * @param prevx 
	 * @param prevy
	 * @param nextx
	 * @param nexty
	 * @return
	 */
	private boolean isCrossWall(int prevx, int prevy, int nextx, int nexty) {
		
		List<Integer> values = null;
		try {
			/**
			 * 查找KD树，寻找最近Constant.NEARST_WALLS_NUM个点
			 */
			values = LoadWallInfo.KD_TREE.nearest(new double[]{prevx, prevy} , Constant.NEARST_WALLS_NUM);
		} catch (KeySizeException e) {
			LOGGER.error("KDTree KeySizeException");
//			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			LOGGER.error("KDTree IllegalArgumentException");
//			e.printStackTrace();
		}
		//
		if (values == null) return false;
		Point prevPoint = new Point(prevx, prevy);
		Point nextPoint = new Point(nextx, nexty);
		for (int val : values) {
			//根据点的信息得到墙体信息
			WallInfo wall = LoadWallInfo.WALLS_INFO_MAP.get(val);
			if (wall == null) {
				LOGGER.error("[PARTICLE: ERROR IN KDTREE TO MAP]");
				continue;
			}
			//判断是否穿墙，若其中一个穿墙，则直接返回true。
			if (wall.isCrossWall(prevPoint, nextPoint)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 计算定位结果,加权均值
	 * @return 定位坐标结果均值
	 */
	private Point estimate() {
		
		double x = 0, y = 0;
		for (Particle particle : particles) {
			x += particle.weight()*particle.positionX();
			y += particle.weight()*particle.positionY();
		}
		return new Point((int)x, (int)y);
	}
	
	/**
	 * 判断是否需重采样
	 * @param N 重采样阈值
	 * @return
	 */
	private boolean isNeedResample(int N) {
		
		int ret = 1;
		double sqrtSum = 0.0;
		for (Particle p : particles) {
			sqrtSum += Math.pow(p.weight(), 2);
		}
		ret = (int)(1.0/sqrtSum);
		return ret >= N ? false : true;
	}
	
	/**
	 * 重采样算法
	 * multinomial resample
	 * 常见的一共有四种重采样算法，其它几种重采样算法可见：
	 * https://pythonhosted.org/filterpy/_modules/filterpy/monte_carlo/resampling.html
	 */
	private void simpleResample () {
		
		LOGGER.info("PARTICLE RESAMPLE...");
		double[] cumsum = new double[this.particleNumbers];
		/**
		 * 求累积和
		 */
		cumsum[0] = this.particles[0].weight();
		for (int i = 1; i < this.particleNumbers; i++) {
			cumsum[i] = cumsum[i-1] + this.particles[i].weight();
		}
		cumsum[this.particleNumbers-1] = 1.0;
		
		Particle[] newParticles = new Particle[this.particleNumbers];
		double weightSum = 0.00000001;
		/**
		 * O(n*log(n))
		 */
		for (int i = 0; i < this.particleNumbers; i++) {
			int idx = binarySearch(cumsum, CommonUtils.randomDoubleNumber());
			idx = idx >= 0 ? idx : this.particleNumbers-1; //
			newParticles[i] = this.particles[idx].clone();
			weightSum += newParticles[i].weight();
//			LOGGER.debug("IDX: {}, weight: {}", idx, newParticles[i].weight());
		}
		/**
		 * 归一化权重
		 */
		for (int i = 0; i < newParticles.length; i++) {
			double weight = newParticles[i].weight()/weightSum + 0.00000001;
			newParticles[i].weight(weight);
		}
		this.particles = newParticles;
	}
	
	/**
	 * 重采样算法中需要的二分查找
	 * @param cumsum
	 * @param d
	 * @return
	 */
	private static int binarySearch(double[] cumsum, double d){
		
        int low = 0, high = cumsum.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (Double.compare(cumsum[mid], d) > 0) {
            	high = mid - 1;
            } else if (Double.compare(cumsum[mid], d) < 0) {
            	low = mid + 1;
            } else {
            	return mid;
            }
        }
        return low-1;
    }
	
	//test
	public static void main(String[] args) {
		double[] cumsum = {0.1,0.4,0.7,0.9,1.0};
//		double i;
//		System.out.println(binarySearch(cumsum, i = 0.99));
//		System.out.println(i);
//		System.out.println(CommonUtils.gaussian(10, 6));
		for (int i = 0; i < 20; i++) {
			System.out.println(binarySearch(cumsum, CommonUtils.randomDoubleNumber()));
		}
	}
}
