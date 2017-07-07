package cn.edu.uestc.indoorlocation.common;

public interface Constant {

	/**
	 * 消息类型
	 */
	//作为参考用，可根据实际进行更改
	
	//登录    客户端=>服务器
	byte TYPE_LOGIN = 0;
	
	//登录成功/失败   服务器=>客户端
	byte TYPE_RESPONSE_LOGIN_SUCCESS = 1;
	byte TYPE_RESPONSE_LOGIN_FAIL = 2;
	
	//登出  客户端
	byte TYPE_LOGOUT = 5;
	//登出成功/失败   服务器=>客户端
	byte TYPE_RESPONSE_LOGOUT_SUCCESS = 6;
	byte TYPE_RESPONSE_LOGOUT_FAIL = 7;
	
	//请求定位  客户端=>服务器
	byte TYPE_LOCATION = 14;
	
	//定位结果   服务器=>客户端
	byte TYPE_RESPONSE_LOCATION = 15;
	//定位失败   服务器=>客户端
	byte TYPE_RESPONSE_LOCATION_FAIL = 16;
	
	///////////////////////////////////////////////////
	
	byte TYPE_REGISTER = 20;
	byte TYPE_ERSPONSE_REGISTER_SUCCESS = 21;
	byte TYPE_RESPONSE_REGISTER_FALIL = 22;
	
	String SESSION_ID = "sessionid";
}
