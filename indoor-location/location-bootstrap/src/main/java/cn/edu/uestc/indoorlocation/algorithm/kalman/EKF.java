package cn.edu.uestc.indoorlocation.algorithm.kalman;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Jama.Matrix;
import cn.edu.uestc.indoorlocation.algorithm.Filter;
import cn.edu.uestc.indoorlocation.algorithm.common.CommonUtils;
import cn.edu.uestc.indoorlocation.algorithm.common.Constant;
import cn.edu.uestc.indoorlocation.algorithm.common.Sensors;
import cn.edu.uestc.indoorlocation.dao.model.Point;

/**
 * EKF实现
 * @author wensen
 *
 */
public class EKF implements Filter{

	private static Logger LOGGER = LoggerFactory.getLogger(EKF.class);
	
	/**
	 * 步长预测、x坐标、y坐标
	 */
	private Matrix currentX;
	
	/**
	 * FK, 即对currentX求偏导后的值
	 */
	private Matrix Fk = Matrix.identity(3, 3);
	
	/**
	 * 状态噪声
	 */
	private Matrix Qk;
	
	/**
	 * 协方差结果
	 */
	private Matrix Pk;
	
	/**
	 * 对观测值求偏导，即分别对观测航向角（手机可采集），x、y坐标(由指纹定位得到)
	 * 初始化为一个3*3的单位矩阵
	 */
	private static Matrix H ;
	/**
	 * 3*3的单位矩阵
	 */
	private static Matrix I3 = Matrix.identity(3, 3);
	
	private Point prevPoint = null;
	public EKF() {
		
		double[][] d = {{0, 1.0, 0},
						{0, 0, 1.0}};
		H = new Matrix(d);
		Qk = Matrix.identity(3, 3);
		Pk = Matrix.identity(3, 3);
	}
	
	/**
	 * 初始化值使用指纹定位得到
	 * 
	 * @point 指纹定位得到的坐标
	 */
	public void init(Point point) {
		
		if (prevPoint != null) {
			
			double[][] prez = {
					{Constant.NORMAL_STEP_LENGTH},
					{point.getX()*0.8 + prevPoint.getX()*0.2},
					{point.getY()*0.8 + prevPoint.getY()*0.2}
			};
			
			currentX = new Matrix(prez);
			prevPoint = new Point((int)(point.getX()*0.8 + prevPoint.getX()*0.2), 
					(int)(point.getY()*0.8 + prevPoint.getY()*0.2));
			
		} else {
			double[][] prez = {
				{Constant.NORMAL_STEP_LENGTH},
				{point.getX()},
				{point.getY()}
			};
		
			currentX = new Matrix(prez);
			prevPoint = (Point)point.clone();
		}
	}
	
	/**
	 * 预测阶段
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
	 * 目前仅使用航向角作为方向判断
	 * 为什么不用陀悬仪呢？觉得若是使用陀悬仪，可能会有迭代的误差
	 * 航向角就可以得到方向，何乐而不为呢
	 * 航向角毕竟是由客户端的传感器收集，所以我设置了10度的误差
	 * 同时步长的估计中，我设置了+/-5cm的误差，这些误差都当做噪声	
	 * @param sensors 传感器数据
	 * @param al 
	 */
	public void predict(Sensors sensor, double al) {
		
		//test
		double alpha = Constant.STEP_LENGTH_ALPHA;
		/**
		 * 取得航向角
		 */
		double theta = CommonUtils.azimuthAngleFromSensor(sensor) + Math.toRadians(
				CommonUtils.generateRandomIntegerRangeNumber(0, 5));;
		double costheta = Math.cos(theta);
		double sintheta = Math.sin(theta);
		
		/**            **                                              **
		 *             *  (1-alpha)*(上一步的步长) + alpha*(本次预测的步长)     *
		 * currentX =  *  上一步的x坐标  + 本次步长*cos(航向角)                  *
		 *             *  上一步的y坐标 + 本次步长*sin(航向角)                   *
		 *             **												**
		 */
		double lk = (1 - Constant.STEP_LENGTH_ALPHA)*currentX.get(0, 0) + 
						alpha*CommonUtils.lengthEstimate(sensor) + 
					      CommonUtils.generateRandomIntegerRangeNumber(0, 5);
		double xk  = currentX.get(1, 0) + lk * costheta;
		double yk  = currentX.get(2, 0) + lk * sintheta;
		double[][] pre = { {lk}, {xk}, {yk} };
		currentX = new Matrix(pre);
		
		/**         1-alpha              0       0
		 * Fk =    (1-alpha)*cos(theta)  1       0
		 *         (1-alpha)*sin(theta)  0       1 
		 */        
		Fk.set(0, 0, (1-alpha));
		Fk.set(1, 0, (1-alpha)*costheta);
		Fk.set(2, 0, (1-alpha)*sintheta);
		
		/**
		 * Qk = diag(Vw, Vs*costheta^2, Vs*sintheta^2)
		 * 
		 * Vw为步长预测协方差
		 * Vs为步
		 */
		double Vw = 5;
		double Vs = 5;
		
		Qk.set(0, 0, Vw);
		Qk.set(1, 1, Math.pow(costheta, 2) * Vs);
		Qk.set(2, 2, Math.pow(sintheta, 2) * Vs);
		
		/**
		 * Qk = Fk*Pk*(Fk)^T + Qk
		 */
		Pk = Fk.times(Pk).times(Fk.transpose()).plus(Qk);
	}
	
	/**
	 * update阶段
	 * @param fingerRet 指纹定位结果
	 * @param R   指纹定位协方差
	 * @return
	 */
	public Point update(Point fingerRet, Matrix R) {
		
		/**
		 * Kk = Pk*transpose(H)*inverse(H*Pk*transpose(H) + R)
		 */
		Matrix tmp = H.times(Pk).times(H.transpose()).plus(R);
		//LOGGER.debug("SERVER EKF: {}", Arrays.toString(Pk.getArray()));
//		Pk.print(3, 3);
		Matrix Kk  = Pk.times(H.transpose()).times(tmp.inverse());
		
		/**
		 * currentX = currentX + Kk*(zk - H*currentX)
		 */
		double[][] z = {{fingerRet.getX()}, {fingerRet.getY()}};
		Matrix zk = new Matrix(z);
		Matrix t2 = zk.minus(H.times(currentX));
		currentX = currentX.plus(Kk.times(t2));
		
		/**
		 * Pk = (I3 - Kk*H)Pk
		 */
		Pk = (I3.minus(Kk.times(H))).times(Pk);
		
		Point point = new Point((int)currentX.get(1, 0), (int)currentX.get(2, 0));
		return point;
	}
}
