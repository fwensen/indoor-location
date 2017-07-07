package cn.edu.uestc.indoorlocation.algorithm.common;

import org.junit.Assert;
import org.junit.Test;

import cn.edu.uestc.indoorlocation.dao.model.Point;

public class TestWallInfo {

	WallInfo wall = new WallInfo(4, 10, 3, 10,6,6);
	
	@Test
	public void testIsCrossWall() {
		
		Point p1 = new Point(0,0);
		Point p2 = new Point(5,5);
		Assert.assertTrue(wall.isCrossWall(p1, p2));
		Point p3 = new Point(3,3);
		Assert.assertTrue(!wall.isCrossWall(p1, p3));
		
		Point p4 = new Point(6,2);
		Point p5 = new Point(3,10);
		Assert.assertTrue(wall.isCrossWall(p4, p5));
		Point p6 = new Point(4,2);
		Point p7 = new Point(7,2);
		Assert.assertTrue(!wall.isCrossWall(p6, p7));
	}
}
