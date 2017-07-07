package cn.edu.uestc.indoorlocation.algorithm;

import Jama.Matrix;
import cn.edu.uestc.indoorlocation.algorithm.common.Sensors;
import cn.edu.uestc.indoorlocation.dao.model.Point;

/**
 * 
 * @author wensen
 *	Filter包括卡尔曼滤波器、粒子滤波器等
 *
 */
public interface Filter {
	
	/**
	 * 滤波器初始化时，在定位时，初始化有两种情况
	 * 1、滤波器类初始化时
	 * 2、最开始定位时，需要使用指纹定位结果来更新初始化结果
	 * @param point
	 */
	void init(Point point);
	
	/**
	 * 预测过程，使用传感器数据，根据PDR原理预测下一步
	 * @param sensor 传感器数据
	 * @param al 
	 */
	void predict(Sensors sensor, double al);
	
	/**
	 * 根据测量值（这里使用指纹定位结果），更新滤波器预测结果
	 * @param fingerRet 指纹定位结果
	 * @param R 指纹定位协方差
	 * @return 定位结果
	 */
	Point update(Point fingerRet, Matrix R);
	
}
