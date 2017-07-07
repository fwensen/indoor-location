package cn.edu.uestc.indoorlocation.dao.model;

import java.util.UUID;

/**
 * 坐标点,以cm为单位
 * x, y, z
 * @author vincent
 *
 */
public class Point {

	private final int x;
	private final int y;
	private final int z;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getZ() {
		return this.z;
	}
	
	public Point setX(int x) {
		return new Point(x, this.y, this.z);
	}
	
	public Point setY(int y) {
		return new Point(this.x, y, this.z);
	}
	
	public Point setZ(int z) {
		return new Point(this.x, this.y, z);
	}
	
	public String toString() {
		return "[Point: " + this.x + ", " + this.y + ", " + this.z+"]";
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || o.getClass() != getClass()) return false;
		Point p = (Point)o;
		return p.x == x && p.y == y && p.z == z;
	}
	
	/**
	 * hashcode生成
	 * 如果仅用x, y生成可能会有相同的hash值吗?
	 */
	@Override
	public int hashCode() {
		
		int ret = 17;
		ret += 31*ret + x;
		ret += 31*ret + y;
		ret += 31*ret + z;
//		ret += UUID.randomUUID().hashCode();
		return ret;
	}
	
	@Override
	public Point clone() {
		
		Point point = new Point(this.x, this.y);
		
		return point;
	}
}
