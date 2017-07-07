package cn.edu.uestc.indoorlocation.algorithm.common;

import cn.edu.uestc.indoorlocation.dao.model.Point;

/**
 * the point of a static path, comtains the angle
 * @author vincent
 *
 */
public class PathPoint {

	private final Point point;
	private final int angle;
	
	public PathPoint(Point p, int angle) {
		this.point = p;
		this.angle = angle;
	}
	
	public int getX() {
		return this.point.getX();
	}
	
	public int getY() {
		return this.point.getY();
	}
	
	public int angle() {
		return this.angle;
	}
	
	public String toString() {
		return this.point + " angle: " + angle;
	}
}
