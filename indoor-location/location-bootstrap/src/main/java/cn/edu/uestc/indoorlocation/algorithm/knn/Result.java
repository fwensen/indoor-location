package cn.edu.uestc.indoorlocation.algorithm.knn;

import cn.edu.uestc.indoorlocation.dao.model.Point;
/**
 * 计算距离或相似度后的结果值
 * @author vincent
 *
 */
public class Result {

	/**
	 * 计算出来的值，如欧拉距离值，余弦相似度值
	 */
	private final double value;
	/**
	 * 虽然这里final没什么用，呵呵
	 */
	private final Point point;
	
	public Result (double val, Point p) {
		this.value = val;
		this.point = p;
	}
	
	public double value() {
		return this.value;
	}
	
	public Point point() {
		return this.point;
	}
	
	public String toString() {
		String ret = "{Result: " + this.value + " ";
		ret += this.point + "}";
		return ret;
	}
}
