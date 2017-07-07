package cn.edu.uestc.indoorlocation.algorithm.common;

import de.biomedical_imaging.edu.wlu.cs.levy.CG.Editor;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KDTree;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeySizeException;

/**
 * 对KDTree进行了改造，使它变成一个线程安全的类
 * 
 * @author wensen
 *
 */
public class KDTreeTool {

	static final class UnmodifiableKDTree<T> extends KDTree<T> {

		private KDTree<T> kdTree;
		
		public UnmodifiableKDTree(int k) {
			super(k);
		}
		
		/**
		 * 这里只能曲线救国
		 * @param tree
		 */
		public void setKdTree(KDTree<T> tree) {
			this.kdTree = tree;
		}
		
		@Override
		public T nearest(double[] key) throws KeySizeException {
			return kdTree.nearest(key);
		}
		
		@Override
		public java.util.List<T> nearest(double[] key, int n) 
				throws KeySizeException, IllegalArgumentException {
			return kdTree.nearest(key, n);
		}
		
		@Override
		public java.util.List<T> nearestEuclidean(double[] key, double dist) 
				throws KeySizeException {
		
			return kdTree.nearestEuclidean(key, dist);
		}
		
		@Override
		public java.util.List<T> nearestHamming(double[] key, double dist) 
				throws KeySizeException {
			return kdTree.nearestHamming(key, dist);
		}
		
		@Override
		public java.util.List<T> range(double[] lowk, double[] uppk)
				throws KeySizeException{
			return kdTree.range(lowk, uppk);
		}
		
		@Override
		public T search(double[] key) throws KeySizeException {
			return kdTree.search(key);
		}
		
		@Override
		public void delete(double[] key) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void delete(double[] key, boolean optional) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void insert(double[] key, T value) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void	edit(double[] key, Editor<T> editor) {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * 不可更改的KDTree,类似于Collections中工具
	 * 如：Collections.unmodifiableMap（map）
	 * @param tree
	 * @return
	 */
	public static KDTree unmodifiableKDTree(KDTree tree) {
		
		UnmodifiableKDTree kdTree = new KDTreeTool.UnmodifiableKDTree(2);
		kdTree.setKdTree(tree);
		return kdTree;
	}
}
