package cn.edu.uestc.indoorlocation.dao.model;

/**
 * 对应数据库中的fingerprint表
 * @author vincent
 *
 */
public class PlainFingerPrint {

	private final long ap_id;
	private final int position_x;
	private final int position_y;
	private final int position_z;
	
	public PlainFingerPrint(long id, int x, int y, int z) {
		this.ap_id = id;
		this.position_x = x;
		this.position_y = y;
		this.position_z = z;
	}
	
	public long ap_id() {
		return this.ap_id;
	}
	
	public Point point() {
		return new Point(this.position_x, this.position_y, this.position_z);
	}
	
	public int position_x() {
		return this.position_x;
	}
	
	public int position_y() {
		return this.position_y;
	}
	
	public int position_z() {
		return this.position_z;
	}
	
	public String toString() {
		return "{ap_id: " + ap_id + " x: " + position_x + " y: " + position_y  + " }";
	}
	
}
