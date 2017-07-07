package cn.edu.uestc.indoorlocation.algorithm.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.biomedical_imaging.edu.wlu.cs.levy.CG.KDTree;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeyDuplicateException;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeySizeException;

public class LoadWallInfo {

private static Logger LOGGER = LoggerFactory.getLogger(LoadWallInfo.class);
	
	public LoadWallInfo() {}
	/**
	 * http://home.wlu.edu/~levys/software/kd/
	 * 使用二维信息的KD树,若将楼层考虑进来时，可使用三维的KD树
	 */
	public static KDTree KD_TREE = null;
	/**
	 * 用于查询，由KD树中得到相关的值后（本项目中是一个int值），然后使用
	 * 该int值可查询到对应墙体的墙体信息
	 */
	public static Map<Integer, WallInfo> WALLS_INFO_MAP = null;
	
	/**
	 * 解析输入文件中的墙体信息
	 * 文件格式中每行为
	 * x1:x2:y1:y2;x:y
	 * 其中x1,x2代表左右，y1,y2代表下上，(x,y)为典型坐标
	 * 
	 * @param fileName
	 * @param latch 闭锁
	 * @throws KeyDuplicateException 
	 * @throws KeySizeException 
	 */
	public static void initWallsInfo(String fileName, CountDownLatch latch)  {
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			DocumentBuilder builder = factory.newDocumentBuilder();   
			Document doc = builder.parse(LoadPaths.class.getClassLoader().getResourceAsStream(fileName)); 

			KDTree tmpKDTree = new KDTree(2);
			Map<Integer, WallInfo> tmpMap = new HashMap<>();
			int n = 0;
			NodeList nl = doc.getElementsByTagName("value");
			for (int i = 0; i < nl.getLength(); i++) {
				
				String sx1 = doc.getElementsByTagName("x1").item(i).getFirstChild().getNodeValue();
				String sx2 = doc.getElementsByTagName("x2").item(i).getFirstChild().getNodeValue();
				String sy1 = doc.getElementsByTagName("y1").item(i).getFirstChild().getNodeValue();
				String sy2 = doc.getElementsByTagName("y2").item(i).getFirstChild().getNodeValue();
				String sx = doc.getElementsByTagName("x").item(i).getFirstChild().getNodeValue();
				String sy = doc.getElementsByTagName("y").item(i).getFirstChild().getNodeValue();
				
				int x1 = Integer.parseInt(sx1);
				int x2 = Integer.parseInt(sx2);
				int y1 = Integer.parseInt(sy1);
				int y2 = Integer.parseInt(sy2);
				int x = Integer.parseInt(sx);
				int y = Integer.parseInt(sy);
				
				double[] kd = new double[2];
				kd[0] = x; kd[1] = y;
				try {
					tmpKDTree.insert(kd, i);;
				} catch (KeySizeException e) {
					LOGGER.error("[KDTREE: THE SIZE OF KEY ERROR]");
					e.printStackTrace();
				} catch (KeyDuplicateException e) {
					LOGGER.error("[KDTREE: DUPLICATE KEY]");
					continue;
				}
				tmpMap.put(n++, new WallInfo(x1, x2, y1, y2, x, y));
			}
			/**
			 * 将两个结构设置成不可更改的，只可访问不可更改，这样使用者才不会乱用
			 */
			KD_TREE = KDTreeTool.unmodifiableKDTree(tmpKDTree);
			WALLS_INFO_MAP = Collections.unmodifiableMap(tmpMap);
		} catch (SAXException e1) {
			LOGGER.error("SAXException {}", e1);
		} catch (IOException e1) {
			LOGGER.error("IOException {}", e1);
		} catch (ParserConfigurationException e1) {
			LOGGER.error("ParserConfigurationException {}", e1);
		} finally {
			latch.countDown();
		}
	}
}
