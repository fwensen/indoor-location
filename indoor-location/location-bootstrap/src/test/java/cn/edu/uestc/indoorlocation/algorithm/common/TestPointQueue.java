package cn.edu.uestc.indoorlocation.algorithm.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.edu.uestc.indoorlocation.dao.model.Point;

public class TestPointQueue {

	PointQueue queue;
	
	@Before
	public void init() {
		queue = new PointQueue();
	}
	
	
	@Test
	public void testEnqueue() {
		
		for (int i = 0; i < Constant.SLOW_QUEUE_LENGTH; i++) {
			Point t = new Point(i, i);
			Point r = queue.enqueue(t, Constant.FAST_QUEUE_LENGTH);
			System.out.println("t: " + t + " r: " + r);
			Assert.assertTrue(t.equals(r));
		}
		
		for (int i = 0; i < 5; i++) {
			Point p = new Point(i + 100, i + 100);
			System.out.println(queue.enqueue(p, Constant.SLOW_QUEUE_LENGTH));
			System.out.println(queue.enqueue(p, Constant.FAST_QUEUE_LENGTH));
			System.out.println(queue.enqueue(p, Constant.NORMAL_QUEUE_LENGTH));
			System.out.println("**********************************************");
		}
	}
}
