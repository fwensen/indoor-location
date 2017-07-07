package cn.edu.uestc.indoorlocation.algorithm;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.algorithm.common.Sensors;
import cn.edu.uestc.indoorlocation.dao.model.Point;
import cn.edu.uestc.indoorlocation.dao.model.Rss;

/**
 * 定位类的顶层接口
 * @author vincent
 *
 */
public interface Location {

	/**
	 * 预测坐标
	 * @param rssis 客户端发送来的采集的信号值
	 * @return 定位结果
	 */
	
	Point predict(List<Rss> rssis, List<Sensors> sensors);
	
	/**
	 * 
	 * @param json 客户端参数
	 * @return 定位结果
	 */
	Point predict(JSONObject json);
}
