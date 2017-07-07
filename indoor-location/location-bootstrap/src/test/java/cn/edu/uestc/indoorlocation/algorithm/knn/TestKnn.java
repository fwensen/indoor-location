package cn.edu.uestc.indoorlocation.algorithm.knn;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.edu.uestc.indoorlocation.algorithm.particle_filter.PF;
import cn.edu.uestc.indoorlocation.dao.datasource.JdbcTemplateLocationDataSource;
import cn.edu.uestc.indoorlocation.dao.model.Point;

public class TestKnn {

	KNNLocation knn;
	ProcessTestRssDatas rss;
	ProcessTestRssDatas iterator;
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
		knn = new KNNLocation("cosine", 20);
		knn.setDataSource(new JdbcTemplateLocationDataSource(new JdbcTemplate(dataSource())));
		knn.setFilter(new PF(100));
		iterator = new ProcessTestRssDatas("allrssis_v4.txt");
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
	public void testKnn() {
		
		System.out.println(" origin        predict" 
					+ "        error" + "     " + "escape");
		System.out.println("----------------------------------------------");
		Iterator<MRSS> ite = iterator.iterator();
		if (ite == null) return;
		
		List<Double> errorList = new ArrayList<Double>();
		List<Integer> escapeList = new ArrayList<Integer>();
		while (ite.hasNext()) {
			MRSS rss = ite.next();
			Point origin = rss.getPoint();
//			System.out.println("json: " + rss.getJson());
			long escape = System.currentTimeMillis();
			Point p = knn.predict(rss.getJson());
			
			escape = System.currentTimeMillis() - escape;
			double error = computeError(origin, p);
			errorList.add(error);
			escapeList.add((int)escape);
			StringBuilder builder = new StringBuilder();
			String originX = trimString("" + origin.getX(), true);
			builder.append(originX);
			builder.append(",");
			builder.append(trimString("" + origin.getY(), true));
			builder.append("     ");
			builder.append(trimString("" + p.getX(), true));
			builder.append(",");
			builder.append(trimString("" + p.getY(), true));
			
			builder.append("     ");
			builder.append(trimString("" + error, false));
			builder.append("     ");
			builder.append("" + escape);
			
			System.out.println(builder.toString());
			predictx.append(p.getX()+",");
			predicty.append(p.getY()+",");
			testx.append(origin.getX()+",");
			testy.append(origin.getY()+",");
			
//////////////以下代码用于测试
//			String orig = origin.getX() + "," + origin.getY() + "\n";
//			String pred = p.getX() + "," + p.getY() + "\n"; 
//			FileOutputStream stream = null;
//			try {
//				stream = new FileOutputStream("./escape.txt", true);
//				stream.write((orig).getBytes());
////				stream.write((pred).getBytes());
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			try {
//				stream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			/////////////////////////////////////
			
			
		}
		System.out.println(predictx.toString());
		System.out.println(predicty.toString());
		System.out.println("origin");
		System.out.println(testx.toString());
		System.out.println(testy.toString());
		Collections.sort(errorList);
		errorList.remove(errorList.size()-1);
//		errorList.remove(errorList.size()-1);
		double sum =0.0;
		double escapeSum = 0;
		for (double r : errorList) sum += r;
		for (int e : escapeList)escapeSum += e;
		System.out.println("Average error: " + sum/errorList.size());
		System.out.println("Average escape: " + escapeSum/escapeList.size());
	}
}
