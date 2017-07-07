package cn.edu.uestc.indoorlocation.dao.datasource;

/**
 * 数据库操作语句
 * @author vincent
 *
 */
public interface SQLOperation {

	/************************       查询操作                  *****************************/	
	
	/**
	 * 查询特定用户名的用户信息
	 */
	String SELECT_USER_BY_USERNAME = "SELECT user_name, user_password, user_email, user_tel FROM USER WHERE user_name=?";
	/**
	 * 查询特定用户是否登录
	 */
	String SELECT_ISLOGIN_BY_USERNAME = "SELECT is_login FROM user WHERE user_name = ?";
	/**
	 * 查询特定用户的注册时间
	 */
	String SELECT_REGISTERDATE_BY_USERNAME = "SELECT register_date FROM user WHERE user_name=?";
	/**
	 * 查询特定用户的email
	 */
	String SELECT_EMAIL_BY_USERNAME = "SELECT user_email FROM user WHERE user_name=?";
	/**
	 * 查询特定用户的电话
	 */
	String SELECT_TEL_BY_USERNAME = "SELECT user_tel FROM user WHERE user_name=?";
	/**
	 * 查询fingerprint中所有信息(ap_id, x, y, z)
	 */
	String SELECT_APINFO_FROM_FINGERPRINT = "SELECT ap_id, position_x, position_y, position_z FROM fingerprint";
	/**
	 * 根据finger_id查询指纹数据(ap_id, x, y, z)
	 */
	String SELECT_APINFO_BY_FINGERID = "SELECT ap_id, position_x, position_y, position_z FROM fingerprint WHERE ap_id=?";
	/**
	 * 根据ap_id查询相关的rss信息(这是一个字符串形式，需客户端自行解析)
	 */
	String SELECT_RSSINFO_BY_APID = "SELECT ap_id, rss_detail FROM rssis WHERE ap_id = ?";
	/**
	 * 查询fingerprint表中指纹坐标点数
	 */
	String SELECT_COUNT_FINGERPRINT = "SELECT COUNT(ap_id) FROM fingerprint";
	
	/**
	 * 选择fingerprint表中从start+1开始的num行数据
	 */
	String SELECT_SINGLEPRINT_BY_NUMBER = "SELECT ap_id, position_x, position_y, position_z FROM fingerprint LIMIT ?, 1";
	/**
	 * 查询从start到end条的指纹数据
	 */
	String SELECT_NPRINT_BY_NUMBER = "SELECT ap_id, position_x, position_y, position_z FROM fingerprint LIMIT ?, ?";
	
}
