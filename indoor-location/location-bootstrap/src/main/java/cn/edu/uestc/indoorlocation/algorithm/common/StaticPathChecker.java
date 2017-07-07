package cn.edu.uestc.indoorlocation.algorithm.common;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.uestc.indoorlocation.algorithm.knn.KNNLocation;
import cn.edu.uestc.indoorlocation.dao.model.Point;

/**
 * 检测用户的路径是否和静态路径匹配
 * @author vincent
 *
 */
public class StaticPathChecker {

	private static Logger LOGGER = LoggerFactory.getLogger(StaticPathChecker.class);
	
	private LinkedList<PathPoint> queue = new LinkedList<PathPoint>();
	//检验出的相似的路径,避免扫描所有路径
	private StaticPath chosenPath = null;
	private boolean findPath = false;
	public StaticPathChecker() {
		chosenPath = new StaticPath();
	}
	
	/**
	 * 返回与路径匹配的点，若没有匹配则返回null，这里注意null的处理
	 * @param pathPoint
	 * @return
	 */
	public Point checker(PathPoint pathPoint) {
		
		int x = pathPoint.getX(), y = pathPoint.getY();
		int angle = pathPoint.angle();
		
		if (queue.size() > 0) {
			PathPoint lastPoint = this.queue.peekLast();
			int distance = (int) Math.sqrt(Math.pow(lastPoint.getX() - x, 2) + 
					Math.pow(lastPoint.getY() + y, 2));
			/**
			 * 距离太近时，不需要进行匹配,也不会入队
			 */
			if (distance < 40) {
				return new Point(x, y); 
			}
		}
		
		queue.addLast(new PathPoint(new Point(x, y), angle));
		//队列大小不够时，继续向队列中添加节点
		if (queue.size() <= Constant.MAX_STATIC_PATH_QUEUE_LENGTH + 1) {
			return new Point(x, y);
		}
		
		this.queue.removeFirst();
		return check();
	}
	
	private Point check() {
		
		Point ret = null;
		//test
		this.chosenPath = LoadPaths.paths.get(0);
		if (findPath) {
			return findPath(this.chosenPath);
		} else {
			for (StaticPath sp : LoadPaths.paths) {
				ret = findPath(sp);
				if (ret != null) return ret;
			}
		}
		return ret;	
	}
	
	/**
	 * 对静态路径进行匹配，若匹配，则返回匹配的点，否则返回null
	 * @param sp 静态路径
	 * @return
	 */
	private Point findPath(StaticPath sp) {
		
		List<PathPoint> path = sp.getPath();
		Point result = null;
		
		PathPoint lastPoint = this.queue.removeLast();
		int similarPoint = 0;
		for (PathPoint pp : queue) {
			
			for (int i = 0; i < path.size(); i++) {
				int distance = (int) Math.sqrt(Math.pow(pp.getX() - path.get(i).getX(), 2) + 
						Math.pow(pp.getY() - path.get(i).getY(), 2));
				if (distance < Constant.MAX_DISTANCE_FOR_STATIC_PATH) {
					similarPoint++;
				} else {
					continue;
				}
			}
		}
		/**
		 * 为甚么这么做呢，因为定位时跳跃性有点大，只能根据一条路上相近的
		 * 点来判断是否匹配路径，这里取至少MIN_SIMILAR_NUMBER个点作为依据
		 */
		if (similarPoint < Constant.MIN_SIMILAR_NUMBER){
			this.queue.addLast(lastPoint);
			findPath = false;
			return null;
		} else {
			int minDistance = Integer.MAX_VALUE;
			int minIdx = -1;
			for (int i = 0; i < path.size(); i++) {
				int distance = (int) Math.sqrt(Math.pow(lastPoint.getX() - path.get(i).getX(), 2) + 
						Math.pow(lastPoint.getY() - path.get(i).getY(), 2));
				
				if (distance < Constant.MAX_DISTANCE_FOR_STATIC_PATH) {
					if (distance < minDistance) {
						minDistance = distance;
						minIdx = i;
					}
					findPath = true;
				}
			}
			if (minIdx != -1) {
				this.chosenPath = sp;
				Point ret = new Point(path.get(minIdx).getX(), path.get(minIdx).getY());
				LOGGER.info("STATIC PATH POINT: found!!!!!");
				this.queue.addLast(new PathPoint((Point)ret.clone(), path.get(minIdx).angle()));
				return ret;
			}
			
			return null;
		}
	}
}
