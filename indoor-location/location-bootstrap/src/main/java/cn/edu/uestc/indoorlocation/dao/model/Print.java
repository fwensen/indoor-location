package cn.edu.uestc.indoorlocation.dao.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 指纹数据
 * 坐标点和相对应的指纹数据
 * @author vincent
 *
 */
public class Print {

	private Point point;
	private List<ArrayList<Rss>> prints = new ArrayList<ArrayList<Rss>>();
	private int N = 0;
	
	public Print(Point point) {
		this.point = point;
	}
	
	public Print(int x, int y) {
		this.point = new Point(x, y);
	}
	
	public void addPrint(List<Rss> rssis) {
		prints.add((ArrayList<Rss>) rssis);
		N++;
	}
	
	public Point point() {
//		return new Point(point.getX(), point.getY(), point.getZ());
		return this.point;
	}
	
	public int length () {
		return this.prints.size();
	}
	
	public List<Rss> get(int idx) {
		
		idx = Integer.compare(idx, this.N) >= 0 ? (idx % N) : idx;
		return this.prints.get(idx);
	}
	
	
	private String printAll() {
		String ret = "";
		
		ret += ("[" + this.point.getX() + ", " + this.point.getY()+  "]\n");
		for (int i = 0; i < N; i++) {
			ret += "{";
			for (Rss rss : this.prints.get(i)) {
				ret += rss.MAC() +"==>" + rss.RSS() + "; ";
			}
			ret += "}\n";
		}
		return ret;
	}
	
	public String toString() {
		return "{Print: " + printAll() + "}";
	}
}
