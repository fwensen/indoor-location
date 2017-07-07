package cn.edu.uestc.indoorlocation.communication.message;

import org.msgpack.annotation.Message;

@Message
public class TestObj {

	int a;
	String b;
	
	public TestObj(int a, String b) {
		this.a = a;
		this.b = b;
	}
	
	
	public int getA() {
		return this.a;
	}
	
	public String getB() {
		return this.b;
	}
}
