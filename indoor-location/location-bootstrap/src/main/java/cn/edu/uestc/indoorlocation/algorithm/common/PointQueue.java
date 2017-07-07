package cn.edu.uestc.indoorlocation.algorithm.common;

import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.uestc.indoorlocation.dao.model.Point;

/**
 * RSS结果的队列
 * @author wensen
 *
 */
public class PointQueue{

	private static Logger LOGGER = LoggerFactory.getLogger(PointQueue.class);
	
	private LinkedList<Point> _queue;
	
	public PointQueue() {
		this._queue = new LinkedList<Point>();
	}
	
	/**
	 * 将定位的结果值插入队列，同时根据level值返回一个计算的结果值
	 * @param p 结果值
	 * @param level 即几种队列长度值，分别对应SLOW_QUEUE_LENGTH, NORMAL_QUEUE_LENGTH和FAST_QUEUE_LENGTH
	 * 和MAX_QUEUE_LENGTH
	 * @return
	 */
	public Point enqueue(Point p, int level) {

		//先判断level是否在范围内
		assert level == Constant.SLOW_QUEUE_LENGTH 
				|| level == Constant.NORMAL_QUEUE_LENGTH 
				|| level == Constant.FAST_QUEUE_LENGTH
				|| level == Constant.MAX_QUEUE_LENGTH;
		/**
		 * 这里仅作了简单的处理，当队列大小小于SLOW_QUEUE_LENGTH时，会将p插入队列，但仅返回原来的值，为了简单
		 */
		
		if (this._queue.size() < Constant.MAX_QUEUE_LENGTH + 1) {  //因为又插入了一个
			return (Point)p.clone();
		} 
		
		Point result = calculateAvgV2(p, level);
		LOGGER.info("PointQueue level: {}, point: {}, result: {}", level, p, result);
		this._queue.addLast(result);
		Point tmp = this._queue.removeFirst();
		tmp = null;   //help gc
		return result;
	}
	
	/**
	 * 根据不同的level计算均值
	 * @param level
	 * @return
	 */
	private Point calculateAvg(Point p,int level) {
		
		Point ret = null;
		//获得倒序iterator
		Iterator<Point> ite = this._queue.descendingIterator();
		int avgX = 0, avgY = 0, avgZ = 0;
		
		int idx = 0;
		int count = 0;
		//因为在开始时就将节点插入了队列尾部，所以这里需要时<=
		while (ite.hasNext() && idx++ <= level)  {
			Point pt = ite.next();
			avgX += pt.getX();
			avgY += pt.getY();
			avgZ += pt.getZ();
			count += 1;
		}
		
		ret = new Point(avgX/count, avgY/count, avgZ/count);
		return ret;
	}
	
	/**
	 * 这里仅针对FAST_QUEUE_LENGTH等于1， 
	 * NORMAL_QUEUE_LENGTH等于2...等的情况，不具有普遍性
	 * @param level
	 * @return
	 */
	private Point calculateAvgV2(Point p,int level) {
		
		Point ret = null;
		//获得倒序iterator
		Object[] array =  this._queue.toArray();
		int avgX = 0, avgY = 0, avgZ = 0;
		switch (level) {
		/**
		 * 最后一点占70%, 倒数第二点占30%
		 */
		case Constant.FAST_QUEUE_LENGTH:
			
			
			Point last = (Point)array[array.length-1];
			ret = new Point((int)(0.7*p.getX()+0.3*last.getX()), 
					(int)(0.7*p.getY()+0.3*last.getY()));
			break;
		/**
		 * 50%, 30%, 20%
		 */
		case Constant.NORMAL_QUEUE_LENGTH:
			Point nlast = (Point)array[array.length-1];
			Point nlast2 = (Point)array[array.length-2];
			
			ret = new Point((int)(0.5*p.getX() + 0.3*nlast.getX() + 0.2*nlast2.getX()), 
					(int)(0.5*p.getY() + 0.3*nlast.getY() + 0.2*nlast2.getY()));
			break;
		/**
		 * 40%,30%,20%,10%
		 */
		case Constant.SLOW_QUEUE_LENGTH:
			
			Point slast = (Point)array[array.length-1];
			Point slast2 = (Point)array[array.length-2];
			Point slast3 = (Point)array[array.length-3];
			ret = new Point((int)(0.4*p.getX() + 0.3*slast.getX() + 0.2*slast2.getX() + 0.1*slast3.getX()), 
					(int)(0.4*p.getX()+0.3*slast.getX()+0.2*slast2.getX()+0.1*slast3.getX()));
			break;
		
		/**
		 * 40%,20%,20%,10%,10%
		 */
		default:
			Point mlast  = (Point)array[array.length-1];
			Point mlast2 = (Point)array[array.length-2];
			Point mlast3 = (Point)array[array.length-3];
			Point mlast4 = (Point)array[array.length-4];
			int x = (int)(0.4*p.getX() + 0.2*mlast.getX() + 0.2*mlast2.getX() + 0.1*mlast3.getX() + 0.1*mlast4.getX());
			int y = (int)(0.4*p.getY() + 0.2*mlast.getY() + 0.2*mlast2.getY() + 0.1*mlast3.getY() + 0.1*mlast4.getY());
			ret = new Point(x, y);
			break;
			
		}
		return ret;
	}
	
	public static void main(String[] args) {
		
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(13);
		queue.add(14);
		queue.add(15);
		Object[] array = queue.toArray();
		for (Object o : array) {
			System.out.println((int)o);
		}
	}
	
}
