package cn.edu.uestc.indoorlocation.algorithm.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.algorithm.knn.AscendingOrderComparator;
import cn.edu.uestc.indoorlocation.algorithm.knn.DescendingOrderComparator;
import cn.edu.uestc.indoorlocation.algorithm.knn.Result;
import cn.edu.uestc.indoorlocation.dao.model.Point;
import cn.edu.uestc.indoorlocation.dao.model.Rss;

/**
 * 工具类
 * @author vincent
 *
 */
public class CommonUtils {
	
	/**
	 * 过滤掉过于小的RSS信号
	 */
	public static final int MIN_RSS_VAL = -100;
	private static Random RANDOM = new Random();
	public static List<Rss> rssFilter(List<Rss> rssis) {
		
		List<Rss> ret = new ArrayList<Rss>();
		for (Rss rss : rssis) {
			if (Integer.compare(rss.RSS(), MIN_RSS_VAL) >= 0) {
				ret.add(rss);
			}
		}
		return ret;
	}
	
	/**
	 * 取得rss1和rss2的MAC交集，
	 * @param rss1
	 * @param rss2
	 * @return
	 */
	public static List<ArrayList<Rss>> macIntersection(List<Rss> rss1, List<Rss> rss2) {
		
		List<ArrayList<Rss>> result = new ArrayList<ArrayList<Rss>>();
		ArrayList<Rss> ret1 = new ArrayList<>();
		ArrayList<Rss> ret2 = new ArrayList<>();
		//假设rss2已排序
		for (Rss rss : rss1) {
			int len = rss2.size();
			for (int i = 0; i < len; i++) {
				if (rss2.get(i).MAC().equals(rss.MAC())) {
					ret1.add(rss);
					ret2.add(rss2.get(i));
					rss2.remove(i);
					break;
				}
			}
		}
		result.add(ret1);
		result.add(ret2);
		return result;
	}
	
	public static List<ArrayList<Rss>> macIntersection2(List<Rss> rss1, List<Rss> rss2) {
		
		List<ArrayList<Rss>> result = new ArrayList<ArrayList<Rss>>();
		ArrayList<Rss> ret1 = new ArrayList<>();
		ArrayList<Rss> ret2 = new ArrayList<>();
		Map<String, Integer> mapForRss1 = new HashMap<String, Integer>();
		Map<String, Integer> mapForRss2 = new HashMap<String, Integer>();
		for (Rss rss : rss1) {
			mapForRss1.put(rss.MAC(), rss.RSS());
		}
		for (Rss rss : rss2) {
			mapForRss2.put(rss.MAC(), rss.RSS());
			if (!mapForRss1.containsKey(rss.MAC())) {
				mapForRss1.put(rss.MAC(), -92);
			}
		}
		for (Map.Entry<String, Integer> entry : mapForRss1.entrySet()) {
			ret1.add(new Rss(entry.getKey(), entry.getValue()));
			if (!mapForRss2.containsKey(entry.getKey())) {
				mapForRss2.put(entry.getKey(), -92);
			}
		}
		
		
		for (Map.Entry<String, Integer> entry : mapForRss2.entrySet()) {
			ret2.add(new Rss(entry.getKey(), entry.getValue()));
		}
		result.add(ret1);
		result.add(ret2);
		return result;
	}
	
	/**
	 * 根据传入的字符串使用欧式距离、余弦相似度等
	 * 计算两个rss的距离或相似度
	 * @param rss1
	 * @param rss2  待匹配指纹
	 * @param point 待匹配指纹坐标
	 * @return corr
	 */
	public static double distance (List<Integer> rss1, List<Integer> rss2, String method) {
		
		if (method.trim().equals("euclidean")) return MathUtils.euclidean(rss1, rss2);
		if (method.trim().equals("cosine"))    return MathUtils.cosine(rss1, rss2);
		if (method.trim().equals("corr"))    return MathUtils.corr(rss1, rss2);
		return 0;
	}

	/**
	 * 高斯函数，用于权重比较
	 * @param ret
	 * @param sigma
	 * @return
	 */
	public static double gaussian(double dist, double sigma) {
		return Math.pow(Math.E, (-dist*dist)/(2*sigma*sigma));
	}
	
	public static double similarity(double dist, double N) {
		return Math.sqrt(dist)/(Math.sqrt(dist) + N*Math.pow(Math.E, -dist));
	}
	
	/**
	 * 对k个结果坐标值作权重的方式得到结果定位点
	 * @param results
	 * @return
	 */
	public static Point weightResult(List<Result> results, String method) {
		
		double retX = 0.0;
		double retY = 0.0;
		double weightSum = 0.0;
		if (!method.equalsIgnoreCase("euclidean")) {
			
			for (Result ret : results) {
				double similarity = gaussian(1.0/ret.value(), 0.9);
//				System.out.println("similarity: " + similarity);
				double x = ret.point().getX();
				double y = ret.point().getY();
				retX += x*similarity;
				retY += y*similarity;
				weightSum += similarity;
			}
			return new Point((int)(retX/weightSum), (int)(retY/weightSum));
			
		} else {
			for (Result ret : results) {
				double gaussian = gaussian(ret.value(), 30);
				System.out.println("distance: " + ret.value());
				double x = ret.point().getX();
				double y = ret.point().getY();
				retX += x*gaussian;
				retY += y*gaussian;
				weightSum += gaussian;
			}
			return new Point((int)(retX/weightSum), (int)(retY/weightSum));
		}
		
	}

	/**
	 * 对k个结果坐标值求均值
	 * @param results
	 * @return
	 */
	public static Point avgResult(List<Result> results) {
		
		int len = results.size();
		int x_sum = 0, y_sum = 0;
		for (Result ret : results) {

			x_sum += ret.point().getX();
			y_sum += ret.point().getY();
		}
		x_sum /= len;
		y_sum /= len;
		return new Point(x_sum, y_sum);
	}
	
	/**
	 * 处理传感器数据，因为每次得到的传感器数据可能会有很多，此时最可能的情况是
	 * 客户端没有步进，超时时，客户端会请求定位，这段时间内可能会有多条传感器数据
	 * 这里仅简单作了均值处理
	 * @param sensors
	 * @return
	 */
	public static Sensors handleSensorDatas(Sensors sensors) {
		
		//TODO
		double azimuthAngle   = 0;
//		boolean isStep        = sensors.get(0).isStep();
		double weAcceration   = 0;
		double nsAcceleration = 0;
		double udAcceleration = 0;
		int timeDiff          = 0;
		return sensors;
//		return new Sensor(azimuthAngle, isStep, weAcceration, nsAcceleration, udAcceleration, timeDiff);
	}
	
	/**
	 * 根据步进间隔得到步进估计
	 * @param timeDiff
	 * @return 步长估计
	 */
	public static double stepLengthEstimate(int timeDiff) {
		
		if (timeDiff >= Constant.SLOW_TIME_DIFF) {
			return Constant.SLOW_STEP_LENGTH;
		} else if (timeDiff >= Constant.NORMAL_TIME_DIFF && timeDiff < Constant.SLOW_TIME_DIFF) {
			return Constant.NORMAL_STEP_LENGTH;
		} else if (timeDiff >= Constant.FAST_TIME_DIFF && timeDiff < Constant.FAST_STEP_LENGTH) {
			return Constant.FAST_STEP_LENGTH;
		}
		
		return Constant.NORMAL_STEP_LENGTH;
	}
	
	/**
	 * 根据步进时间差，得到快慢的level
	 * @param timedff
	 * @return level值， 对应FAST_QUEUE_LENGTH，NORMAL_QUEUE_LENGTH,SLOW_QUEUE_LENGTH
	 */
	public static int levelEstimate(int timediff) {
		
		if (timediff >= Constant.SLOW_TIME_DIFF) {
			return Constant.SLOW_QUEUE_LENGTH;
		} else if (timediff >= Constant.NORMAL_TIME_DIFF && timediff < Constant.SLOW_TIME_DIFF) {
			return Constant.NORMAL_QUEUE_LENGTH;
		} else {
			return Constant.FAST_QUEUE_LENGTH;
		}
	}
	
	/**
	 * 解析json包中的数据
	 * @param sensorArray
	 * @return
	 */
	public static List<Sensors> parseSensorInfo(JSONArray sensorArray) {
		
		List<Sensors> sensors = new ArrayList<>();
		int len = sensorArray.size();
		
		for (int i = 0; i < len; i++) {
			JSONObject obj = sensorArray.getJSONObject(i);
			Sensors ss = new Sensors(obj.getInteger("stepNo"), obj.getInteger("timeDiff"));
			JSONArray sensorinfo = obj.getJSONArray("sensorInfo");
			int len2 = sensorinfo.size();
			for (int j = 0; j < len2; j++) {
			
				JSONObject o = sensorinfo.getJSONObject(j);
				double azimuthAngle   = o.getDouble("azimuthAngle");
				double weAcceration   = o.getDouble("weAcce");
				double nsAcceleration = o.getDouble("nsAcce");
				double udAcceleration = o.getDouble("udAcce");
				Sensor sensor = new Sensor(azimuthAngle, 
						weAcceration, nsAcceleration, udAcceleration);
				ss.addSensor(sensor);
			}
			sensors.add(ss);
		}
		return sensors;
	}
	
	/**
	 * 获取json中的RSS信息
	 * @param json
	 * @return
	 */
	public static List<Rss> parseRSSInfo(JSONObject json) {
		
		List<Rss> rssis = new ArrayList<>();
		JSONArray array = json.getJSONArray("rssis");
		
		int len = array.size();
		//取得json中的信号值
		for (int i = 0; i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			Rss rss = new Rss(obj.getString("MAC"), obj.getIntValue("RSS"));
			rssis.add(rss);
		}
		return rssis;
	}
	
	/**
	 * 调试用
	 * @param list
	 */
	public static void printDist(List<Result> list) {
		
		for (Result ret : list) {
			System.out.print(ret.value() + " ");
		}
		System.out.println();
	}
	
	/**
	 * 步长预测
	 * @param sensor
	 * @return
	 */
	public static double lengthEstimate(Sensors sensor) {
		
		int timeDiff = sensor.timediff();
		return stepLengthEstimate(timeDiff);
	}
	
	/**
	 * 获得航向角，获得的方法很简单，找到加速度标准差最大时的航向角即可
	 * 因为这时候才最有可能是运动方向
	 * @param sensor
	 * @return
	 */
	public static double azimuthAngleFromSensor(Sensors sensor) {
		
		Iterator<Sensor> iterator = sensor.iterator();
		double ret = 0;
		double max = Double.MIN_VALUE;
		while (iterator.hasNext()) {
			
			Sensor s = iterator.next();
			double tmp = Math.pow(s.nsAcceleration(), 2) 
								+ Math.pow(s.udAcceleration(), 2) 
								+ Math.pow(s.weAcceration(), 2);
			if (Double.compare(tmp, max) > 0) {
				ret = s.azimuthAngle();
				max = tmp;
			}
		}
		return ret;
	}
	
	
	/**
	 * 选择排序，使用TreeSet实现
	 * @param results 待排序值
	 * @param k k个最小或最大
	 */
	@SuppressWarnings("unchecked")
	public static List<Result> selectSort(List<Result> results, int k, String distanceMethod) {
		
		int len = results.size();
		assert len >= k;
		
		TreeSet<Result> set;
		//选择排序方式
		Comparator<Result> comp = distanceMethod.trim().equals("euclidean") ? 
				new AscendingOrderComparator() : new DescendingOrderComparator();
		
		set = new TreeSet<Result>(comp);
		//初始时插入k个数据
		for (int i = 0; i < k; i++) {
			set.add(results.get(i));
		}
		
		for (int  i = k; i < len; i++) {

			set.add(results.get(i));
			if (set.size() > k) {
				set.remove(set.last());
			}
		}
		
		List<Result> ret = new ArrayList<>();
		Iterator<Result> ite = set.iterator();
		while (ite.hasNext()) {
			ret.add(ite.next());
		}
		return ret;
	}	
	
	/**
	 * 生成-(start + diff)到start + diff区间的随机整型数
	 * @param start
	 * @param diff
	 * @return
	 */
	public static int generateRandomIntegerRangeNumber(int start, int diff) {
		
		if (diff <= 0) diff = 1;
		int next = RANDOM.nextInt(diff);
		boolean positive = RANDOM.nextBoolean();
		int ret = start + next*(positive ? 1 : -1);
		return ret;
	}
	
	public static int generateRandomIntegerNumber(int num) {
		assert num > 0;
		return RANDOM.nextInt(num);
	}
	
	/**
	 * 生成在0到2*PI之间的随机角度
	 * @return
	 */
	public static double generateRandomAngle() {
		double rand = RANDOM.nextDouble();
		return rand*Math.PI*2;
	}
	
	/**
	 * 产生0到1之间的小数
	 * @return
	 */
	public static double randomDoubleNumber() {
		return RANDOM.nextDouble();
	}
	
	public static void main(String[] args) {
		Random random = new Random();
		for (int i = 1; i < 50; i += 1) {
//			double m = RANDOM.nextDouble()*300;
			System.out.println(i + " === " + gaussian(i, 30));;
		}
//		System.out.println(stepLengthEstimate(700));
	}
}