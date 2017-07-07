package cn.edu.uestc.indoorlocation.algorithm.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 保存传感器数据
 * @author wensen
 *
 */
public class Sensors implements Comparable<Sensors>{

	/**
	 * 步进序号，为0表示未步进，否则步进序号从1开始计数
	 */
	private final int _stepno;
	private List<Sensor> _sensors;
	private final int _timediff;
	
	public Sensors(int no, int diff) {
		this._stepno = no;
		this._timediff = diff;
		this._sensors = new ArrayList<>();
	}
	
	public int stepNo() {
		return this._stepno;
	}
	
	public int timediff() {
		return this._timediff;
	}
	
	public void addSensor(Sensor sensor) {
		this._sensors.add(sensor);
	}
	
	public Iterator<Sensor> iterator() {
		return this._sensors.iterator();
	}
	
	public String toString() {
		
		String ret = "{";
		ret += "stepno=" + this._stepno + " sensorinfo:";
		int len = this._sensors.size();
		for (int i = 0; i < len -1; i++) {
			ret += this._sensors.get(i) + ",";
		}
		ret += this._sensors.get(len-1) + "}";
		return ret;
	}

	@Override
	public int compareTo(Sensors o) {
		return this._stepno - o._stepno;
	}
}
