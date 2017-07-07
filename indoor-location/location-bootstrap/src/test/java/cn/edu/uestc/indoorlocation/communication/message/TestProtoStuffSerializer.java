package cn.edu.uestc.indoorlocation.communication.message;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestProtoStuffSerializer {

	Serializer serializer;
	Message msg;
	
	@Before
	public void setUp() {
		serializer = new ProtoStuffSerializer();
		msg = new Message();
		msg.type((byte)2);
		TestObj obj = new TestObj(2,"hello");
		msg.data(obj);
	}
	
	@Test
	public void testSerializer() {
		
		byte[] bytes = serializer.serialize(msg);
		Message out = serializer.deserialize(bytes, Message.class);
		assertEquals(msg.type(), out.type());
		assertEquals(((TestObj)msg.data()).getA(), ((TestObj)msg.data()).getA());
		System.out.println(((TestObj)msg.data()).getA() + "   " + ((TestObj)msg.data()).getB());
	}
	
}
