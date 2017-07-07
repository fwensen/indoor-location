package cn.edu.uestc.indoorlocation.communication.message;

/**
 * 客户端和服务器端通信的Message
 * @author vincent
 *
 */
public class Message {


//	private int length;
	/**
	 * 
	 * @see cn.edu.uestc.indoorlocation.common.Constant
	 */
	private byte type;
	
//	private String mac;
//	private int rss;
//	private float accelerateX;
//	private float accelerateY;
//	private float accelerateZ;
//	
//	private String userName;
//	private String password;
//
//	private int resultX;
//	private int reaultY;
	
	/**
	 * 消息数据：
	 * type为0时：
	 * type为1时：
	 * type为2时
	 */
	private Object data;

	public Message() {}
	
	public Message(byte type, Object data) {
//		this.length = len;
		this.type = type;
		this.data = data;
	}
	
//	public int length() {
//		return this.length;
//	}
//	
//	public void length(int len) {
//		this.length = len;
//	}
	
	public void type(byte type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @see cn.edu.uestc.indoorlocation.common.Constant
	 */
	public byte type() {
		return this.type;
	}
	
	public void data(Object data) {
		this.data = data;
	}
	
	public Object data() {
		return this.data;
	}
	
	public String toString() {
		
		return "Message{type: " + type 
				+ " data: " + data + "}";
	}
}
