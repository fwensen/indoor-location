package cn.edu.uestc.indoorlocation.communication.message;

public class RssMsg {

	private String mac;
	private int rss;
	
	public RssMsg(String mac, int rss) {
		this.mac = mac;
		this.rss = rss;
	}
	
	public String getMac() {
		return this.mac;
	}
	
	
	public int getRss() {
		return this.rss;
	}
	
	public String toString() {
		return "RssMsg {" + this.mac + "  " + this.rss + "}";
	}
}