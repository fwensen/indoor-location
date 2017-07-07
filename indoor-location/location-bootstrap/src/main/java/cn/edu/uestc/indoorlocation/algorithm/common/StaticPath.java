package cn.edu.uestc.indoorlocation.algorithm.common;

import java.util.ArrayList;
import java.util.List;

/**
 * contain the static path point
 * @author vincent
 *
 */
public class StaticPath {

	private List<PathPoint> path = new ArrayList<PathPoint>();
	
	public void addPoint(PathPoint p) {
		this.path.add(p);
	}
	
	public List<PathPoint> getPath() {
		return new ArrayList<PathPoint>(this.path);
	}
	
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		for (PathPoint p : path) {
			builder.append(p);
		}
		return builder.toString();
	}
}
