package cn.edu.uestc.indoorlocation.algorithm.common;

public interface Constant {

	double STEP_LENGTH_ALPHA = 0.7;

	int SLOW_STEP_LENGTH = 52;
	int NORMAL_STEP_LENGTH = 63;
	int FAST_STEP_LENGTH = 74;
	
	////现在还未进行测试
	/**
	 * 快速步进时的时间间隔
	 * 400-550 s
	 */
	int FAST_TIME_DIFF = 400;
	
	/**
	 * 正常步进时间间隔
	 * 550-630s
	 */
	int NORMAL_TIME_DIFF = 550;
	
	/**
	 * 慢速步进时间间隔
	 * >= 630s
	 */
	int SLOW_TIME_DIFF = 630;
	
	int FAST_QUEUE_LENGTH = 1;
	
	int NORMAL_QUEUE_LENGTH = 2;
	
	int SLOW_QUEUE_LENGTH = 3;
	
	int MAX_QUEUE_LENGTH = 4;
	
	/**
	 * NN定位的误差范围
	 * 直接使用指纹定位的误差在300cm以内
	 */
	int ERROR_FOR_NN = 300;
	
	/**
	 * 最近的墙数目
	 */
	int NEARST_WALLS_NUM = 5;
	
	int INITIAL_TIMES = 6;
	/**
	 * 当没有步进的次数在NO_STEP_TIMES次数以内则会使用平均值作为结果
	 */
	int NO_STEP_TIMES = 5;
	/**
	 * 当大于MAX_NO_STEP_COUNT时则不再扩展队列，这时大致结果已经出来了
	 */
	int MAX_NO_STEP_COUNT = 80;
	/**
	 * 在判断静止情况下定位点属于哪个队列时的最大距离
	 */
	int MAX_DISTANCE_OF_A_GROUP = 400;
	/**
	 * 用于静态路径匹配时的队列最大长度,即用户行走的路径在达到该值时才开始匹配
	 */
	int MAX_STATIC_PATH_QUEUE_LENGTH = 10;
	
	int MAX_DISTANCE_FOR_STATIC_PATH = 150;
	
	int MAX_ANGLE_FOR_STATIC_PATH = 15;
	
	int MIN_SIMILAR_NUMBER = 8;
	/**
	 * 记录前几个定位点的数目
	 */
	int PREVIOUS_POINT_SIZE = 8;
	/**
	 * 减少指纹匹配数目中的最小距离，见KNNLocation.isMatchPreviousPointQueue()方法
	 */
	int MAX_MATCH_DISTANCE = 400;
}
