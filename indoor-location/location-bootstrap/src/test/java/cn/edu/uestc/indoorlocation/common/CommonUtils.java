package cn.edu.uestc.indoorlocation.common;

import java.util.Random;

import cn.edu.uestc.indoorlocation.algorithm.common.Sensor;
import cn.edu.uestc.indoorlocation.algorithm.common.Sensors;

public class CommonUtils {

	public static int counter = 1;
	
	
	public static Sensors buildSensorsData() {
		
		Sensors sensors = new Sensors(counter++, 200);
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
			Sensor sensor = new Sensor(Math.PI/2, random.nextDouble(),  
					random.nextDouble(), random.nextDouble());
			sensors.addSensor(sensor);
		}
		return sensors;
	}
	
}
