package cn.edu.uestc.indoorlocation.algorithm.knn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.edu.uestc.indoorlocation.algorithm.knn.ProcessTestRssDatas.MRSSIterator;
import cn.edu.uestc.indoorlocation.dao.model.Point;


public class LineRSSIterator implements Iterable<JSONObject>{

	private static int RSS_LIMIT = 25;
	private String filename;
//	private Random RANDOM = new Random();
	public LineRSSIterator(String filename) {
		this.filename = filename;
	}
	@Override
	public Iterator<JSONObject> iterator() {

		try {
			return new MRSSIterator(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	class MRSSIterator implements Iterator{

		String filename;
		Scanner scanner;
		FileInputStream in;
		boolean hasNext = false;
		
		public MRSSIterator(String filename) throws FileNotFoundException {
			this.filename = filename;
			in = new FileInputStream(this.filename);
			scanner = new Scanner(in);
			hasNext = true;
		}
		
		@Override
		public boolean hasNext() {
			return hasNext;
		}

		private JSONObject generateJson(Map<String, List<Integer>> map) {
			
			Random random = new Random();
			JSONObject json = new JSONObject();
			JSONArray array = new JSONArray();
			for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
				JSONObject obj = new JSONObject();
				obj.put("MAC", entry.getKey());
				List<Integer> list = entry.getValue();
//				Collections.sort(list);
				obj.put("RSS", list.get(random.nextInt(list.size())));
//				obj.put("RSS", list.get(list.size()/2));
				array.add(obj);
			}
			json.put("rssis", array);
			json.put("isStep", false);
			json.put("hasRSS", true);
			
			JSONArray array2 = new JSONArray();
			JSONObject obj2 = new JSONObject();
			obj2.put("stepNo", 1);
			obj2.put("timeDiff", 478);
			
			JSONArray array3 = new JSONArray();
			JSONObject obj3 = new JSONObject();
			obj3.put("nsAcce", -0.150);
			obj3.put("udAcce", 2.29);
			obj3.put("azimuthAngle", 0.218);
			obj3.put("weAcce", -1.04);
			array3.add(obj3);
			obj2.put("sensorInfo", array3);
			array2.add(obj2);
			
			json.put("sensors", array2);
			map = null;
			return json;
		}
		
		@Override
		public Object next() {
			
			Map<String, List<Integer>> map = new HashMap<>();
//			int nextRandom = random.nextInt(n);
			int n = 1;
			for (int i = 0; i < n; i++) {
				
				String []macs = scanner.nextLine().trim().split("#");
//				System.out.println(Arrays.toString(macs));
				
				for (int j = 0; j < macs.length; j++) {
//					System.out.println("#" + macs[j]);
					String[] m = macs[j].trim().split("%");
					
					List<Integer> list;
					if (!map.containsKey(m[0])) {
						list = new ArrayList<>();
						list.add(Integer.parseInt(m[1]));
					} else {
						list = map.get(m[0]);
//						System.out.println("!" + Arrays.toString(m) + " m1:" + m[1]);
						list.add(Integer.parseInt(m[1]));
					}
					map.put(m[0], list);
				}
			}
//			scanner.nextLine();
			
			if (!scanner.hasNext()) {
				hasNext = false;
				try {
					in.close();
					scanner.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return generateJson(map);
		}
	}
}
