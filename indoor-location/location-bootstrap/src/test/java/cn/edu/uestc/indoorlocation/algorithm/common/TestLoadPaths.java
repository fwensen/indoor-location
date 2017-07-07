package cn.edu.uestc.indoorlocation.algorithm.common;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class TestLoadPaths {

	@Test
	public void testLoadPaths() {
		
		CountDownLatch latch = new CountDownLatch(1);
		LoadPaths.loadStaticPath("paths.xml", latch);
		System.out.println("path: ");
		System.out.println(LoadPaths.paths);
	}
}
