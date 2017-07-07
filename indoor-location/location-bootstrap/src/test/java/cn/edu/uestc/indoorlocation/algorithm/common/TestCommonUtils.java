package cn.edu.uestc.indoorlocation.algorithm.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import cn.edu.uestc.indoorlocation.algorithm.knn.Result;

/**
 * 
 * @author vincent
 *
 */
public class TestCommonUtils {

	
	
	@Test
	public void testSelectSort() {
		
		Random random = new Random();
		List<Result> rs = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			rs.add(new Result(random.nextDouble(), null));
		}
		
		for (Result r : rs) {
			System.out.print(r.value() + " ");
		}
		System.out.println();
		List<Result> ret1 = CommonUtils.selectSort(rs, 3, "euclidean");
		System.out.print("euclidean: ");
		for (Result r : ret1) {
			System.out.print(r.value() + " ");
		}
		System.out.println();
		
		List<Result> ret2 = CommonUtils.selectSort(rs, 3, "cosine");
		System.out.print("cosine: ");
		for (Result r : ret2) {
			System.out.print(r.value() + " ");
		}
		System.out.println();
	}
}
