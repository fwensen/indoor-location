package cn.edu.uestc.indoorlocation.algorithm.common;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import cn.edu.uestc.indoorlocation.dao.model.Point;

public class TestStaticPathChecker {

	StaticPathChecker checker = null;
	
	@Before
	public void init() {
		checker = new StaticPathChecker();
		CountDownLatch latch = new CountDownLatch(1);
		LoadPaths.loadStaticPath("paths.xml", latch);
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testChecker() {
		
		Random rand = new Random();
		for (int i = 0; i < 40; i++) {
			Point p = new Point(i*50 + rand.nextInt(10), 1300 + rand.nextInt(10));
			PathPoint pp = new PathPoint(p, 0);
			Point ret = checker.checker(pp);
			if (ret != null) {
				System.out.println(ret);
			} else {
				System.out.println("fail");
			}
		}
		
		for (int i = 100; i <= 1050; i+=50) {
			System.out.print(1550 + "," + i + "," + 90 + ";");
		}
		for (int i = 1550; i <= 2100; i += 50) {
			System.out.print(i + "," + 1100 + "," + 0 + ";");
		}
		
	}
}
