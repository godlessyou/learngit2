package com.tmkoo.searchapi.util;

import java.util.List;

/**
 * 执行任务，实现Runable方式
 *
 */
public class ExcuteTask implements Runnable {

	String opt = null;
	Integer custId = 0;
	String custName = null;
	List<String> appNames = null;
	String message = null;
	String url=null;	

	GongGaoTmDataService tmDataService = null;
	
	XuzhanService xuzhanService=null;

	public ExcuteTask() {

	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public List<String> getAppNames() {
		return appNames;
	}

	public void setAppNames(List<String> appNames) {
		this.appNames = appNames;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public GongGaoTmDataService getTmDataService() {
		return tmDataService;
	}

	public void setTmDataService(GongGaoTmDataService tmDataService) {
		this.tmDataService = tmDataService;
	}

	@Override
	public void run() {
		try {
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public XuzhanService getXuzhanService() {
		return xuzhanService;
	}

	public void setXuzhanService(XuzhanService xuzhanService) {
		this.xuzhanService = xuzhanService;
	}

}