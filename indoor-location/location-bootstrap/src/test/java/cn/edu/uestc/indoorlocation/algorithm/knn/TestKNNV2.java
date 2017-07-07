package cn.edu.uestc.indoorlocation.algorithm.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.algorithm.particle_filter.PF;
import cn.edu.uestc.indoorlocation.dao.datasource.JdbcTemplateLocationDataSource;
import cn.edu.uestc.indoorlocation.dao.model.Point;

public class TestKNNV2 {

	KNNLocation knn;
	LineRSSIterator rss;
	LineRSSIterator iterator;
	private StringBuilder predictx = new StringBuilder();
	private StringBuilder predicty = new StringBuilder();
	
	private StringBuilder testx = new StringBuilder();
	private StringBuilder testy = new StringBuilder();
	
	public static BasicDataSource dataSource() {
		
		BasicDataSource source = new BasicDataSource();
		source.setDriverClassName("com.mysql.jdbc.Driver");
		source.setUrl("jdbc:mysql://localhost:3306/indoorlocation");
		source.setUsername("root");
		source.setPassword("rootvincent");
		source.setInitialSize(5);
		source.setMaxActive(10);
		return source;
	}
	
	@Before
	public void init() {
		knn = new KNNLocation("cosine", 6);
		knn.setDataSource(new JdbcTemplateLocationDataSource(new JdbcTemplate(dataSource())));
		knn.setFilter(new PF(100));
		iterator = new LineRSSIterator("rss3.txt");
	}
	
	private double computeError(Point p1, Point p2) {
		
		double pow1 = Math.pow(p1.getX() - p2.getX(), 2);
		double pow2 = Math.pow(p1.getY() - p2.getY(), 2);
		return Math.sqrt(pow1 + pow2);
	}
	
	private String trimString(String str, boolean isCoordinate){
		
		if (isCoordinate) {
			if (str.length() < 4) {
				return str + " ";
			} else {
				return str;
			}
		} else {
			if (str.length() >= 9) {
				return str.substring(0, 9);
			} else {
				int len = str.length();
				int left = 9 - len;
				for (int i = 0; i < left; i++) str += " ";
				return str;
			}
		}
	}
	@Test
	public void testKnn(){
		System.out.println(" origin        predict" 
				+ "        error" + "     " + "escape");
	System.out.println("----------------------------------------------");
	Iterator<JSONObject> ite = iterator.iterator();
	if (ite == null) return;
	
	List<Double> errorList = new ArrayList<Double>();
	
	while (ite.hasNext()) {
		JSONObject rss = ite.next();
//		Point origin = rss.getPoint();
//		System.out.println("json: " + rss.getJson());
		long escape = System.currentTimeMillis();
		Point p = knn.predict(rss);
		escape = System.currentTimeMillis() - escape;
		
		StringBuilder builder = new StringBuilder();
		builder.append(trimString("" + p.getX(), true));
		builder.append(",");
		builder.append(trimString("" + p.getY(), true));
		
		builder.append("     ");
		builder.append("" + escape);
		
		System.out.println(builder.toString());
		predictx.append(p.getX()+",");
		predicty.append(p.getY()+",");
	}
	
	System.out.println(predictx.toString());
	System.out.println(predicty.toString());
	double sum =0.0;
	}
}
