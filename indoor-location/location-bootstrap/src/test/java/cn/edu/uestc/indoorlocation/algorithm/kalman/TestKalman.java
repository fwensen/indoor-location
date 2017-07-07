package cn.edu.uestc.indoorlocation.algorithm.kalman;

import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;
import cn.edu.uestc.indoorlocation.algorithm.common.Constant;
import cn.edu.uestc.indoorlocation.algorithm.common.Sensors;
import cn.edu.uestc.indoorlocation.common.CommonUtils;
import cn.edu.uestc.indoorlocation.dao.model.Point;

public class TestKalman {

	EKF kalman;
	
	@Before
	public void init() {
		kalman  = new EKF();
	}
	
	@Test
	public void testKalman() {
		
		Point point = new Point(1000, 1000);
		kalman.init(point);
		
		for (int i = 1; i < 5; i++) {
			Sensors ss = CommonUtils.buildSensorsData();
			kalman.predict(ss, 0.1);
			Point p  = new Point(1000 , 1000 + i*Constant.NORMAL_STEP_LENGTH);
			double[][] r = {{0.1, 0}, {0, 0.1}};
			Matrix R = new Matrix(r);
			Point ret = kalman.update(p, R);
			System.out.println(ret);
		}
//		System.out.println(Math.sin(Math.PI/2));
	}
}
