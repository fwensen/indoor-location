package cn.edu.uestc.indoorlocation.algorithm.knn;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.dao.model.Point;

public class MRSS {

	private Point point;
	private JSONObject json;
	
	public MRSS(Point p, JSONObject j) {
		this.point = p;
		this.json = j;
	}
	
	public Point getPoint() {
		return this.point;
	}
	
	public JSONObject getJson() {
		return this.json;
	}
}
