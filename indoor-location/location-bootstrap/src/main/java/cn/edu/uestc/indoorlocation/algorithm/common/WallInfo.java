package cn.edu.uestc.indoorlocation.algorithm.common;

import java.io.InputStream;
import java.util.Scanner;

import cn.edu.uestc.indoorlocation.dao.model.Point;

/**
 * 存储墙体信息，同时可用于判断线段是否穿越墙体
 * @author wensen
 *
 */
public class WallInfo {

	/**
	 *  ************************* y2
	 * 	*						*
	 *  *	              墙体			*
	 *  *		(x,y)			*
	 *  *						*
	 *  ************************* y1
	 *  x1                      x2
	 */
	private final int x1;
	private final int x2;
	private final int y1;
	private final int y2;
	/**
	 * 墙体典型坐标点
	 */
	private final int x;
	private final int y;
	
	public WallInfo(int x1, int x2, int y1, int y2, int x, int y) {
		
		this.x1 = Math.min(x1, x2);
		this.x2 = Math.max(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.x = x;
		this.y = y;
	}
	
	private boolean isInWall(int x, int y) {
		
		if (x1 <= x && x <= x2 && y1 <= y && y <= y2) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断给定的两点是否穿墙
	 * @param a 当前点坐标
	 * @param b 下一点坐标
	 * @return 是否穿墙
	 */
	public boolean isCrossWall(Point a, Point b) {
		/**
		 *  ************************
		 *  *					   *
		 *  *					   *
		 *  *		a   		 b****a
		 *  *		*			   *
		 *  ************************					   *
		 *  		*b  
		 */
		
		if (x2 < Math.min(a.getX(), b.getX()) || x1 > Math.max(a.getX(), b.getX())) {
			return false;
		}
		
		if (y2 < Math.min(a.getY(), b.getY()) || y1 > Math.max(a.getY(), b.getY())) {
			return false;
		}
		
		/**
		 * 如果a在墙外，b在墙内，则穿墙了
		  
		 **/
		if (isInWall(b.getX(), b.getY()) && !isInWall(a.getX(), a.getY())) {
			return true;
		}
		/**
		 * 如果b在墙外，a在墙内，则穿墙了
		 **/
		if (isInWall(a.getX(), a.getY()) && !isInWall(b.getX(), b.getY())) {
			return true;
		}
		
		/**
		 * 剩余情况，直接对四条线段判断相交
		 * p2                    p4
		 * ***********************
		 * *					 |
		 * *					 | 
		 * *	 wall			 |
		 * *					 |
		 * *				     |
		 * ***********************
		 * p1					p3	
		 */
		//墙的四个顶点
		Point p1 = new Point(x1, y1);
		Point p2 = new Point(x1, y2);
		Point p3 = new Point(x2, y1);
		Point p4 = new Point(x2, y2);

		if (isCrossLine(a, b, p1, p2)) return true;
		if (isCrossLine(a, b, p1, p3)) return true;
		if (isCrossLine(a, b, p2, p4)) return true;
		if (isCrossLine(a, b, p3, p4)) return true;
		return false;
	}
	
	/**
	 * 参考http://blog.csdn.net/rickliuxiao/article/details/6259322
	 * 判断两条线段是否相交(a, b)与(c, d)
	 * 两条线段相交有两种情况
	 * 1、交叉
	 *  *
	 *   *
	 * ************
	 *     *
	 *      *
	 * 2、一线段的端点在另一线段上
	 *  ***************
	 *  		*
	 *  		*
	 *  		*
	 * @param a 线段1的起点
	 * @param b 线段1的终点
	 * @param c 线段2的起点
	 * @param d 线段2的终点
	 * @return true表示相交， false表示不相交
	 */
	private boolean isCrossLine(Point a, Point b, Point c, Point d) {
		
		if (Math.max(a.getX(), b.getX()) < Math.min(c.getX(), d.getX())) {
			return false;
		}
		
		if (Math.max(a.getY(), b.getY()) < Math.min(c.getY(), d.getY())) {
			return false;
		}
		
		if (Math.max(c.getX(), d.getX()) < Math.min(a.getX(), b.getX())) {
			return false;
		}
		
		if (Math.max(c.getY(), d.getY()) < Math.min(a.getY(), b.getY())) {
			return false;
		}
		
		if (multi(c, b, a)*multi(b, d, a) < 0) {
			return false;
		}
		
		if (multi(a, d, c)*multi(d, b, c) < 0) {
			return false;
		}
		return true;
	}
	
	private int multi(Point a, Point b, Point c) {
		
		return (a.getX() - c.getX())*(b.getY() - c.getY()) - 
				(b.getX() - c.getX())*(a.getY() - c.getY());
	}
	
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("[WALLINFO: ");
		builder.append("x: " + x1 + "->" + x2);
		builder.append(",y: " + y1 + "->" + y2 + "]\n"); 
		return builder.toString();
	}

	private void testCrossLine() {
		//1
		Point p1 = new Point(0,0);
		Point p2 = new Point(3,3);
		
		Point q1 = new Point(0,2);
		Point q2 = new Point(2,0);
		assert true == isCrossLine(p1, p2, q1, q2);
		
		Point w1 = new Point(3,0);
		Point w2 = new Point(2,1);
		assert false == isCrossLine(p1,p2, w1,w2);
	}
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args) {
		WallInfo wall = new WallInfo(1,2,3,4, 6,7);
//		wall.testCrossLine();
	}
}
