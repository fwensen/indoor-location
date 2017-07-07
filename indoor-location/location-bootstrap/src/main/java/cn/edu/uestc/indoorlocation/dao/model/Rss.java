package cn.edu.uestc.indoorlocation.dao.model;

/**
 * 
 * MAC地址、RSS信号强度对,对应一个ibeacon的信号值
 * @author vincent
 *
 */
public class Rss implements Comparable{

	/**
	 * MAC值
	 */
	private final String MAC;
	/**
	 * RSS值
	 */
	private final int RSS;
	
	public Rss(String mac, int rss) {
		this.MAC = mac;
		this.RSS = rss;
	}
	
	public String MAC() {
		return this.MAC;
	}
	
	public int RSS() {
		return this.RSS;
	}
	
	public Rss MAC(String mac) {
		return new Rss(mac, this.RSS);
	}
	
	public Rss RSS(int rss) {
		return new Rss(this.MAC, rss);
	}
	
	public String toString() {
		return "[Rss: " + this.MAC + ", " + this.RSS + "]";
	}

	@Override
	public int compareTo(Object o) {
		Rss other = (Rss)o;
		return MAC.compareTo(other.MAC);
	}
}
