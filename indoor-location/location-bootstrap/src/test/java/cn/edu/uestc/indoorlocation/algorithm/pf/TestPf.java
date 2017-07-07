package cn.edu.uestc.indoorlocation.algorithm.pf;

import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;
import cn.edu.uestc.indoorlocation.algorithm.Filter;
import cn.edu.uestc.indoorlocation.algorithm.common.Constant;
import cn.edu.uestc.indoorlocation.algorithm.common.Sensors;
import cn.edu.uestc.indoorlocation.algorithm.particle_filter.PF;
import cn.edu.uestc.indoorlocation.common.CommonUtils;
import cn.edu.uestc.indoorlocation.dao.model.Point;

public class TestPf {

Filter filter;
	
	@Before
	public void init() {
		filter  = new PF(10);
	}
	
	@Test
	public void testKalman() {
		
		Point point = new Point(1000, 1000);
		filter.init(point);
		
		for (int i = 1; i < 10; i++) {
			Sensors ss = CommonUtils.buildSensorsData();
			filter.predict(ss, 0.1);
			Point p  = new Point(1000 , 1000 + i*Constant.NORMAL_STEP_LENGTH);
			double[][] r = {{20, 0}, {0, 11}};
			Matrix R = new Matrix(r);
			Point ret = filter.update(p, R);
			System.out.println(ret);
		}
	}
}
