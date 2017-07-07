package cn.edu.uestc.indoorlocation.algorithm.common;

/**
 * 客户端传感器信息
 * @author vincent
 *
 */
public class Sensor {

	/**
	 * 航向角
	 */
	private final double azimuthAngle;
	
	/**
	 * 东西方向加速度
	 */
	private final double weAcceration;
	/**
	 * 南北方向加速度
	 */
	private final double nsAcceleration;
	/**
	 * 上下方向加速度
	 */
	private final double udAcceleration;
	
	public Sensor(double azi, double we, double ns, double ud) {
		
		this.azimuthAngle = azi;
		this.weAcceration = we;
		this.nsAcceleration = ns;
		this.udAcceleration = ud;
	}
	
	public double azimuthAngle() {
		return this.azimuthAngle;
	}
	
	public double weAcceration() {
		return this.weAcceration;
	}
	
	public double nsAcceleration() {
		return this.nsAcceleration;
	}
	
	public double udAcceleration() {
		return this.nsAcceleration;
	}
	
	
	public String toString() {
		
		String ret = "[";
		ret +=    "azimuthAngle=" + this.azimuthAngle   + " weAcceration="   + this.weAcceration;
		ret += " nsAcceleration=" + this.nsAcceleration + " udAcceleration=" + this.udAcceleration ;
		ret += "]";
		return ret;
	}
	
}
