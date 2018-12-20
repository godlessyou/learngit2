package com.tmkoo.searchapi.util;

import org.springframework.stereotype.Component;


@Component
public class ServiceUrlConfig {

	//服务的接口
	private String bdymonitorserviceUrl="http://localhost:8080/bdy_monitor_service/interface";
	
	//服务的接口
	private String bdysysmUrl="http://localhost:8080/bdy_sysm/interface";
	
	// 用户案件更新接口
	private String tmcasedataUrl="http://localhost/tmcasedata";
	
	private String dataBaseIp="localhost";
	
	public String getBdymonitorserviceUrl() {
		return bdymonitorserviceUrl;
	}

	public void setBdymonitorserviceUrl(String bdymonitorserviceUrl) {
		this.bdymonitorserviceUrl = bdymonitorserviceUrl;
	}

	public String getTmcasedataUrl() {
		return tmcasedataUrl;
	}

	public void setTmcasedataUrl(String tmcasedataUrl) {
		this.tmcasedataUrl = tmcasedataUrl;
	}

	public String getDataBaseIp() {
		return dataBaseIp;
	}

	public void setDataBaseIp(String dataBaseIp) {
		this.dataBaseIp = dataBaseIp;
		
		//设置链接数据库所用Url中的IP地址
		boolean initFlag=DatabaseUtil.isInitFlag();
		if (!initFlag){
			DatabaseUtil.init(dataBaseIp);
		}
	}

	public String getBdysysmUrl() {
		return bdysysmUrl;
	}

	public void setBdysysmUrl(String bdysysmUrl) {
		this.bdysysmUrl = bdysysmUrl;
	}

}
