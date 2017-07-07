package cn.edu.uestc.indoorlocation.algorithm.knn;

import java.util.Comparator;

/**
 * 升序比较
 * @author vincent
 *
 */
public class AscendingOrderComparator implements Comparator<Result>{

	@Override
	public int compare(Result o1, Result o2) {
		return Double.compare(o1.value(), o2.value());
	}
}
