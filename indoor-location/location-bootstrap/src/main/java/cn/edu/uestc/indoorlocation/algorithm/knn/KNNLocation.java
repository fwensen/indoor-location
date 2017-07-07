package cn.edu.uestc.indoorlocation.algorithm.knn;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import Jama.Matrix;
import cn.edu.uestc.indoorlocation.algorithm.Filter;
import cn.edu.uestc.indoorlocation.algorithm.Location;
import cn.edu.uestc.indoorlocation.algorithm.common.CommonUtils;
import cn.edu.uestc.indoorlocation.algorithm.common.Constant;
import cn.edu.uestc.indoorlocation.algorithm.common.LoadWallInfo;
import cn.edu.uestc.indoorlocation.algorithm.common.PathPoint;
import cn.edu.uestc.indoorlocation.algorithm.common.PointQueue;
import cn.edu.uestc.indoorlocation.algorithm.common.Sensors;
import cn.edu.uestc.indoorlocation.algorithm.common.StaticPathChecker;
import cn.edu.uestc.indoorlocation.algorithm.common.WallInfo;
import cn.edu.uestc.indoorlocation.algorithm.kalman.EKF;
import cn.edu.uestc.indoorlocation.algorithm.particle_filter.PF;
import cn.edu.uestc.indoorlocation.dao.LocationDataSource;
import cn.edu.uestc.indoorlocation.dao.model.Point;
import cn.edu.uestc.indoorlocation.dao.model.Print;
import cn.edu.uestc.indoorlocation.dao.model.Rss;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeySizeException;

/**
 * KNN算法实现
 * @author vincent
 *
 */
public class KNNLocation implements Location{

	private static Logger LOGGER = LoggerFactory.getLogger(KNNLocation.class);
	/**
	 * 数据源，要么静态数据源，要么数据库源
	 */
	private LocationDataSource dataSource;
	/**
	 * 距离或相似度方法:
	 * euclidean: 欧式距离
	 * cosine   ： 余弦相似度
	 */
	private final String distanceMethod;
	/**
	 * KNN的K值
	 */
	private int k;
	
	/**
	 * 卡尔曼滤波/粒子滤波
	 */
	private Filter filter;
	
	/**
	 * 是初几次定位，使用指纹定位值
	 */
	private static int initialTimes = 0;
	
	/**
	 * 均值队列
	 */
	private PointQueue queue;
	
	/**
	 * 是否步进
	 */
	private boolean isStep;
	/**
	 * 持续没有步进次数
	 */
	private int continueNoStep = 0;
	
	private LinkedList<Point> oddNoStepsQueue = new LinkedList<Point>(); 
	
	private LinkedList<Point> evenNoStepsQueue = new LinkedList<Point>(); 
	
	private boolean isStableStepCached = false;
	
	private Point stableCachePoint = null;
	
	private Random RANDOM_NUMBER = new Random();
	
	private static Point currentPosition = null;
	
	private static Point prevPoint = null;
	
	private StaticPathChecker pathChecker = null;
	
	/**
	 * 从指纹数据中随机选择的指纹数目
	 */
	private static final int RANDOM_RSS_NUMBERS = 25;
	
	private Set<Integer> randomRssIndex = new HashSet<>();
	/**
	 * 定位的前几个点(Constant.PREVIOUS_POINT_SIZE),用于本次定位时减少指纹匹配数目
	 */
	private LinkedList<Point> previousPointQueue = new LinkedList<>();
	
	public void setDataSource(LocationDataSource source) {
		this.dataSource = source;
	}

	public void setFilter(Filter filter) {
		if (filter instanceof PF) {
			LOGGER.info("************************FILTER: PARTICLE FILTER");
		} else if (filter instanceof EKF){
			LOGGER.info("************************FILTER: KALMAN FILTER");
		}
		this.filter = filter;
	}
	
	/**
	 * @param method 距离算法：如欧式距离或余弦相似度
	 * @param k  KNN的k值
	 */
	public KNNLocation(String method, int k) {
		this.distanceMethod = method;
		this.k = k;
//		this.filter = new EKF();
		this.queue = new PointQueue();
		pathChecker = new StaticPathChecker();
	}
	
	@Override
	public Point predict(JSONObject json) {
		
		List<Rss> rssis;
		
		boolean hasRss = json.getBoolean("hasRSS");
		if (!hasRss) {
			LOGGER.error("KNN: no client rss data!");
			return null;
		}
//		isStep = false;
		rssis = CommonUtils.parseRSSInfo(json);
		List<Sensors> sensors = new ArrayList<Sensors>(0);
		
		isStep = json.getBoolean("isStep");
		///取得传感器值
		if (isStep) {
			JSONArray sensorArray = json.getJSONArray("sensors");
			sensors = CommonUtils.parseSensorInfo(sensorArray);
		}
		return predict(rssis, sensors);
	}
	
	/**
	 * 定位主要实现
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Point predict(List<Rss> rssis, List<Sensors> sensors) {
		//首先过滤掉客户端信号值中过小的信号值
//		List<Rss> clientRssis = CommonUtils.rssFilter(rssis);
		List<Rss> clientRssis = rssis;
		/**
		 * 对信号值排序（按mac值排序）,排序后可减小后面指纹匹配的复杂度 macIntersection
		 * 因为数据库中的指纹数据已按照MAC值排序
		 */
		Collections.sort(clientRssis);
		List<Result> result = new ArrayList<>();
		//这里会遍历数据源中所有数据
		while (dataSource.hasNext()) {
			//取得一个坐标上的指纹数据
			Print print = dataSource.next();
			//当该指纹点和前几个定位点不接近时，则略过该点
			if (!isMatchPreviousPointQueue(print.point())) {
				continue;
			}

			randomRssIndex.clear();
			//随机选择RANDOM_RSS_NUMBERS个不同的指纹数据
			while (randomRssIndex.size() <= this.RANDOM_RSS_NUMBERS) {
				int idx = this.RANDOM_NUMBER.nextInt(print.length());
				if (!randomRssIndex.contains(idx)) {
					randomRssIndex.add(idx);
					result.add(new Result(distance(clientRssis, print.get(idx)), print.point()));
				}
			}
			result.add(distance(print, clientRssis));
		}
		
		//使用选择排序的算法(这是由于一般k值很小)
		List<Result> kResult = CommonUtils.selectSort(result, this.k, distanceMethod);

		Point fingerResult   = CommonUtils.weightResult(kResult, distanceMethod);

		/////////////////以下代码用于测试
//		String strx = "", stry = "";
//		for (int i = 0; i < kResult.size()-1; i++) {
//			Point p = kResult.get(i).point();
//			strx += p.getX() + ",";
//			stry += p.getY() + ",";
//		}
//		strx += kResult.get(kResult.size()-1).point().getX();
//		stry += kResult.get(kResult.size()-1).point().getY();
//		FileOutputStream stream = null; 
//		try {
//			stream = new FileOutputStream("./escape.txt", true);
//			stream.write((strx + "\n").getBytes());
//			stream.write((stry + "\n").getBytes());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			stream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		//////////////////////////////////////
		
		Point ret = null;
		//对filter进行初始化initialTimes次
		if (++initialTimes <= Constant.INITIAL_TIMES) {

			filter.init(fingerResult);
//			LOGGER.info("SERVER initial {} times result: {}", initialTimes, fingerResult);
			return (prevPoint = fingerResult);
		}
		
		/**
		 * 没有步进时,这时可能用户没有运动，所以将queue大小取到最大
		 */
		if (!isStep) {
			
			fingerResult = this.queue.enqueue(fingerResult, Constant.MAX_QUEUE_LENGTH);
			//********************************
//			fingerResult = stabilizeStillLocation(fingerResult, Constant.MAX_DISTANCE_OF_A_GROUP);
//			fingerResult = this.queue.enqueue(fingerResult, Constant.MAX_QUEUE_LENGTH);
			
			filter.init(fingerResult);
			addPointToPreviousPointQueue(fingerResult);
//			LOGGER.info("KNN: no step, result: {}", fingerResult);
			return (prevPoint = fingerResult);
		}
		
		/**
		 * 当有步进时，计数器清零
		 */
		if (this.continueNoStep > 0) {
			this.continueNoStep = 0;
			//this.prevStepsQueue.clear();
			this.oddNoStepsQueue.clear();
			this.evenNoStepsQueue.clear();
			isStableStepCached = false;
		}
		
		//平滑当前点
		fingerResult = smoothCurrentPoint(fingerResult);
		/**
		 * test pure wknn
		 */
//		if (true) {
//			return fingerResult;
//		}
		Collections.sort(sensors); 
		//这里可能有多组传感器数据
		for (Sensors ss : sensors) {
			filter.predict(CommonUtils.handleSensorDatas(ss), -1);
			double[][] R = {{RANDOM_NUMBER.nextInt(10), 0}, {0, RANDOM_NUMBER.nextInt(10)}};
			/**
			 * 先得到filter update之后的结果值，然后根据timediff，计算滑动队列后的均值
			 */
			if (ret == null) ret = fingerResult;
			ret =  this.queue.enqueue(filter.update(ret, new Matrix(R)),
					CommonUtils.levelEstimate(ss.timediff()));
		}
		//静态路径匹配
		Point tmp = pathChecker.checker(new PathPoint((Point)ret.clone(), 0));
		if (tmp != null) ret = tmp;
		addPointToPreviousPointQueue(ret);
		LOGGER.info("SERVER location result: {}", ret);
		return (prevPoint = ret);
	}
	
	/**
	 * 这里实现数据库中相应指纹数据的选择
	 * 
	 * @param print
	 * @param rssis
	 * @return
	 */
	private Result distance(Print print, List<Rss> rssis) {
		
		double sum = 0.0;
		int len = print.length();
		for (int i = 0; i < len; i++) {
			sum += distance(rssis, print.get(i));
		}
		Result ret = new Result(sum/len, print.point());
		return ret;
	}
	
	/**
	 * 这里的rssis1和rssi2都已经排序（按照mac值排序）
	 * @param rssis1
	 * @param rssis2 指纹数据
	 * @return
	 */
	private double distance(List<Rss> rssis1, List<Rss> rssis2) {
		
		List<ArrayList<Rss>> r = CommonUtils.macIntersection2(rssis1, rssis2);
		List<Integer> rss1 = new ArrayList<>();
		for (Rss t : r.get(0)) rss1.add(t.RSS());
		
		List<Integer> rss2 = new ArrayList<>();
		for (Rss t : r.get(1)) rss2.add(t.RSS());
		
		return CommonUtils.distance(rss1, rss2, this.distanceMethod);
	}
		
	/**
	 * 判断定位后的点是否穿墙
	 * 在粒子滤波中，若没有步进，则会直接使用指纹滤波结果，而穿墙判断仅在
	 * 粒子滤波中实现，故在这里增加一个穿墙判断用于判断在没有步进和有步进之间的情况
	 * 
	 * @param cur
	 * @param nxt
	 * @return
	 */
	private boolean isCrossWall(Point cur, Point nxt) {
		List<Integer> values = null;
		try {
			/**
			 * 查找KD树，寻找最近Constant.NEARST_WALLS_NUM个点
			 */
			values = LoadWallInfo.KD_TREE.nearest(new double[]{cur.getX(), cur.getY()}, Constant.NEARST_WALLS_NUM);
		} catch (KeySizeException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		//
		if (values == null) return false;
		
		for (int val : values) {
			//根据点的信息得到墙体信息
			WallInfo wall = LoadWallInfo.WALLS_INFO_MAP.get(val);
			if (wall == null) {
				LOGGER.debug("[PARTICLE: ERROR IN KDTREE TO MAP]");
				continue;
			}
			//判断是否穿墙，若其中一个穿墙，则直接返回true。
			if (wall.isCrossWall(cur, nxt)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 静止状态下，稳定定位结果
	 * 使用了两个队列，其中一个队列中得到稳定的值，另一个则是变化很大的值。
	 * @param fingerPrint 指纹定位结果
	 * @param minDistance 分类的最小距离
	 * @return 静止结果
	 */
	private Point stabilizeStillLocation(Point fingerResult, int minDistance) {
		
		this.continueNoStep++;
		/**
		 * 初次没有步进时，则将结果放入oddNoStepsQueue中
		 */
		if (this.continueNoStep == 1) {
			this.oddNoStepsQueue.add(fingerResult);
			return fingerResult;
		}
		/**
		 * 第二次持续没有步进时,则会和前一点进行比较
		 */
		if (this.continueNoStep == 2) {
			Point prePoint = this.oddNoStepsQueue.getFirst();
			double distance = Math.sqrt(Math.pow(prePoint.getX()-fingerResult.getX(), 2) + 
					Math.pow(prePoint.getY() - fingerResult.getY(), 2));
			Point ret = null;
			ret = new Point((prevPoint.getX() + fingerResult.getX())/2, 
					(prevPoint.getY() + fingerResult.getY())/2);
			if (distance > minDistance) {
				this.evenNoStepsQueue.add(fingerResult);
			} else {
				this.oddNoStepsQueue.add(fingerResult);
			}
			
			return ret;
		}
		
		/**
		 * 大于2次小于Constant.MAX_NO_STEP_COUNT次持续不步进时
		 */
		if (this.continueNoStep <= Constant.MAX_NO_STEP_COUNT) {
			int closedPointNumsInOddQ = 0;
			int closedPointNumsInEvenQ = 0;
			for (Point point : this.oddNoStepsQueue) {
				double distance = Math.sqrt(Math.pow(point.getX()-fingerResult.getX(), 2) + 
						Math.pow(point.getY() - fingerResult.getY(), 2));
				if (distance <= minDistance) {
					closedPointNumsInOddQ++;
				} 
			}
			
			for (Point p : this.evenNoStepsQueue) {
				double distance = Math.sqrt(Math.pow(p.getX()-fingerResult.getX(), 2) + 
						Math.pow(p.getY() - fingerResult.getY(), 2));
				if (distance <= minDistance) {
					closedPointNumsInEvenQ++;
				} 
			}
			
			if (closedPointNumsInOddQ >= closedPointNumsInEvenQ) {
				this.oddNoStepsQueue.add(fingerResult);
			} else {
				this.evenNoStepsQueue.add(fingerResult);
			}
			return biggerQueueAvg(oddNoStepsQueue, evenNoStepsQueue);
		}
		
		//如果已经持续MAX_NO_STEP_COUNT次未步进，则直接返回缓存值
		if (isStableStepCached) {
			return this.stableCachePoint;
		}
		
		//选择队列大小较大的队列的均值作为结果
		Point ret = biggerQueueAvg(oddNoStepsQueue, evenNoStepsQueue);
		isStableStepCached = true;
		return ret;
	}
	
	private Point biggerQueueAvg(LinkedList<Point> queue1, LinkedList<Point> queue2) {
		if (queue1.size() >= queue2.size()) {
			return averagePointOfQueue(queue1);
		} else {
			return averagePointOfQueue(queue2);
		}
	}
	
	private Point averagePointOfQueue(LinkedList<Point> queue) {
		int x = 0, y = 0;
		for (Point p : queue) {
			x += p.getX();
			y += p.getY();
		}
		x /= queue.size();
		y /= queue.size();
		Point ret = new Point(x, y);
		return ret;
	}
	
	/**
	 * 用于减少指纹定位计算，匹配时取前几个点的临近点作为匹配点，这样可以减少指纹计算
	 * 该方法用于判断当前指纹点是否和前几个点临近
	 * @param curPoint 当前定位点
	 * @return 是否靠近前几个点
	 */
	private boolean isMatchPreviousPointQueue(Point curPoint) {
		//当前几个点队列还未形成时，直接返回true
		if (this.previousPointQueue.size() <= Constant.PREVIOUS_POINT_SIZE) {
			return true;
		}
		
		for (Point prev : this.previousPointQueue) {
			double dis = Math.sqrt(
					Math.pow(curPoint.getX() - prev.getX(), 2) + 
					Math.pow(curPoint.getY() - prev.getY(), 2) + 
					Math.pow(curPoint.getZ() - prev.getZ(), 2));
			//当距离小于等于Constant.MAX_MATCH_DISTANCE，则表示这点是接近的
			if (dis <= Constant.MAX_MATCH_DISTANCE) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 防止指纹定位抖动剧烈,当当前定位点和上一定位点距离大于400时，取平滑值
	 */
	private Point smoothCurrentPoint(Point curPoint) {
		
		if (Double.compare(distanceOf2point(prevPoint, curPoint),  400.0) > 0) {
			return new Point((int)(0.6*prevPoint.getX() + 0.4*curPoint.getX()), 
					(int)(0.6*prevPoint.getY() + 0.4*curPoint.getY()));
		}
		return curPoint;
	}
	
	private double distanceOf2point(Point prev, Point next) {
		return Math.sqrt(Math.pow(prev.getX() - next.getX(), 2.0) + 
						 Math.pow(prev.getY() - next.getY(), 2.0));
	}
	
	private void addPointToPreviousPointQueue(Point point) {
		
		this.previousPointQueue.addLast(point.clone());
		//去除队列头接点直到大小为Constant.PREVIOUS_POINT_SIZE
		while (this.previousPointQueue.size() > Constant.PREVIOUS_POINT_SIZE) {
			this.previousPointQueue.removeFirst();
		}
	}
	
	private void printDetails(List<Result> kResult) {
		for (Result ret : kResult) {
			LOGGER.info("result value: {}", ret.value());
		}
	}
}
