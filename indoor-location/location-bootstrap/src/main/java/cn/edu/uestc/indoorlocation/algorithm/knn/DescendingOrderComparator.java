package cn.edu.uestc.indoorlocation.algorithm.knn;

import java.util.Comparator;

/**
 * 降序实现
 * @author vincent
 *
 */
public class DescendingOrderComparator implements Comparator<Result>{

	@Override
	public int compare(Result o1, Result o2) {
		
		return Double.compare(o2.value(), o1.value());
	}

}
