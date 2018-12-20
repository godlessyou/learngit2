package com.tmkoo.searchapi.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.util.Properties;


import javax.annotation.Resource;

import org.springframework.stereotype.Component;


@Component
public class DatabaseUtil {
	
	private static boolean initFlag = false;
	
	private static String dataBaseIp = "192.168.0.8";
	
	public static String getDataBaseIp() {
		return dataBaseIp;
	}

	public static void setDataBaseIp(String dataBaseIp) {
		DatabaseUtil.dataBaseIp = dataBaseIp;
	}

	public static void init(String ipAddress) {
		
		if (!initFlag){			
			if (ipAddress!=null && !ipAddress.equals("")){
				dataBaseIp=ipAddress;
			}			
			initFlag=true;
		}
		
//		if (!initFlag){
//			// 第一次加载的properties
//			props = FileUtil.readProperties("application.properties");
//			if (props != null) {
//				String value = props.getProperty("dataBaseIp");
//				if (value!=null && !value.equals("")){
//					dataBaseIp=value;
//				}
//			}
//	
//			initFlag = true;
//		}
	}
	
	public static boolean isInitFlag() {
		return initFlag;
	}

	public static void setInitFlag(boolean initFlag) {
		DatabaseUtil.initFlag = initFlag;
	}

	public static Connection getConForHgj() throws SQLException,
			java.lang.ClassNotFoundException {

		String driverName = "com.mysql.jdbc.Driver";
		// String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

		// 第一步：加载MySQL的JDBC的驱动
		Class.forName(driverName);

		// 取得连接的url,能访问MySQL数据库的用户名,密码；test：数据库名		
		String url = "jdbc:mysql://"+ dataBaseIp +":3306/bdy_db?useUnicode=true&characterEncoding=utf-8&autoReconnect=true";
		String username = "bdyuser";
		String password = "123456";

		// 第二步：创建与MySQL数据库的连接类的实例
		Connection con = DriverManager.getConnection(url, username, password);
		return con;
	}

	public static void main(String[] args) {
		try {
			System.out.println(getConForHgj());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
