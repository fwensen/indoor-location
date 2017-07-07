package cn.edu.uestc.indoorlocation.algorithm.common;

import org.junit.Test;

import cn.edu.uestc.indoorlocation.common.CommonUtils;

public class TestGaussionWeight {

	
	@Test
	public void testGaussion() {
		
		for (int i = 1; i < 10; i++) {
			//			double gaussion = CommonUtils.
			System.out.println( 
					cn.edu.uestc.indoorlocation.algorithm.common.
					CommonUtils.gaussian((double)i, 6.0) + "");
		}
	}
}
