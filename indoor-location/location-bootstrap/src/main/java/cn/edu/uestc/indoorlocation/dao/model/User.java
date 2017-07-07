package cn.edu.uestc.indoorlocation.dao.model;

import java.util.Date;

public class User {

	//用户名
	private String userName;
	/*
	 * 没有加密，现在仅仅是普通字符串
	 */
	private String password;
	//用户邮件
	private String userEmail;
	//用户电话
	private String userTel;
	//用户注册时间
	private Date registerDate;
	//用户是否登录
	private boolean isLogin = false;
	
}
