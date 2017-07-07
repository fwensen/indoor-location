package cn.edu.uestc.indoorlocation.dao.model;

/**
 * 对应数据库中的rss信息
 * @author vincent
 *
 */
public class PlainRss {

	private final long ap_id;
	private final String details;
	
	public PlainRss(long id, String dt) {
		this.ap_id = id;
		this.details = dt;
	}
	
	public long ap_id() {
		return this.ap_id;
	}
	
	public String details() {
		return this.details;
	}
	
}
