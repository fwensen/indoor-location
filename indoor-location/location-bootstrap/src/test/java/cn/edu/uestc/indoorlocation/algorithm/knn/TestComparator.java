package cn.edu.uestc.indoorlocation.algorithm.knn;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试比较
 * @author vincent
 *
 */
public class TestComparator {

	Comparator<Result> ascComparator;
	Comparator<Result> descComparator;
	
	@Before
	public void init() {
		ascComparator = new AscendingOrderComparator();
		descComparator = new DescendingOrderComparator();
	}
	
	@Test
	public void testAscendingOrderComparator() {
		
		Result o1 = new Result(1.0, null);
		Result o2 = new Result(2.0, null);
		Assert.assertTrue(ascComparator.compare(o1, o2) < 0);
	}
	
	@Test
	public void testDescendingOrderComparator() {
		
		Result o1 = new Result(1.0, null);
		Result o2 = new Result(2.0, null);
		Assert.assertTrue(descComparator.compare(o1, o2) > 0);
	}
}
