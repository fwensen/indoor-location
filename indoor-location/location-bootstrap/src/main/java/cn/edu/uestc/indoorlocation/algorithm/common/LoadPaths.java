package cn.edu.uestc.indoorlocation.algorithm.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.edu.uestc.indoorlocation.dao.model.Point;

public class LoadPaths {

	public static List<StaticPath> paths;
	
	public static void loadStaticPath(String filename, CountDownLatch latch){
		
		List<StaticPath> tmpPaths = new ArrayList<StaticPath>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			DocumentBuilder builder = factory.newDocumentBuilder();   
			Document doc = builder.parse(LoadPaths.class.getClassLoader().getResourceAsStream(filename)); 
			
			NodeList nl = doc.getElementsByTagName("value"); 
			for (int i = 0; i < nl.getLength(); i++) {
				
				String path = doc.getElementsByTagName("path").item(i).getFirstChild().getNodeValue();
				String[] points = path.split(";");
				StaticPath sp = new StaticPath();
				for (String point : points) {
					String[] p = point.split(",");
					Point pt = new Point(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
					PathPoint pp = new PathPoint(pt, Integer.parseInt(p[2]));
					sp.addPoint(pp);
				}
				tmpPaths.add(sp);
			}
			
			paths = Collections.unmodifiableList(tmpPaths);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}finally {
			latch.countDown();
		}
	}
	
}
