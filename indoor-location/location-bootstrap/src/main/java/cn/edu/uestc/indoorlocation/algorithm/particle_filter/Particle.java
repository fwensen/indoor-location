package cn.edu.uestc.indoorlocation.algorithm.particle_filter;

public class Particle {

	/**
	 * x坐标
	 */
	private int x;
	/**
	 * y坐标
	 */
	private int y;
	private int z;
//	private double orientation;
	/**
	 * 权重
	 */
	private double weight;
	/**
	 * 步长
	 */
	private int stepLength;
	
	public Particle(int x, int y) {
		this.x = x;
		this.y = y;
//		this.orientation = o;
	}
	
	/**
	 * 
	 * @return
	 */
	public int positionX() {
		return this.x;
	}
	
	public void setPositionX(int x) {
		this.x = x;
	}
	
	public int positionY() {
		return this.y;
	}
	
	public void setPositionY(int y) {
		this.y = y;
	}
	
	public int positionZ() {
		return this.z;
	}
	
	public void setPositionZ(int z) {
		this.z = z;
	}
	
	
//	public double orientation() {
//		return this.orientation;
//	}
//	
//	public void orientation(double o) {
//		this.orientation = o;
//	}
	
	public double weight() {
		return this.weight;
	}
	
	public void weight(double w) {
		this.weight = w;
	}
	
	public int stepLength() {
		return this.stepLength;
	}
	
	public void stepLength(int s) {
		this.stepLength = s;
	}
	
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("{ position:");
		builder.append(this.x + ",");
		builder.append(this.y + ",");
		builder.append(this.z + ";");
//		builder.append(" orientation: " + this.orientation);
		builder.append(", weight: " + this.weight);
		builder.append(", step length: " + this.stepLength + "}\n");
		return builder.toString();
	}
	
	@Override
	public Particle clone() {
		
		Particle particle = new Particle(this.x, this.y);
		particle.stepLength(this.stepLength);
		particle.weight(this.weight);
		return particle;
	}
}
