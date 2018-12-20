package com.tmkoo.searchapi.util;


import javax.annotation.Resource;


import org.springframework.stereotype.Component;

import com.tmkoo.searchapi.common.ReturnInfo;
import com.tmkoo.searchapi.common.LoginReturnInfo;
import com.tmkoo.searchapi.common.Globals;


@Component
public class AuthenticationService {
	@Resource
	private ServiceUrlConfig serviceUrlConfig;
	public ReturnInfo authorize(String tokenID) {
		// 返回结果对象
		ReturnInfo rtnInfo = new ReturnInfo();		
		String url=serviceUrlConfig.getBdysysmUrl()+"/auth/authorize?tokenID="+ tokenID;
		System.out.println(url);
		try {
			String jsonString = GraspUtil.getText(url);
			rtnInfo=JsonUtil.toObject(jsonString, LoginReturnInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
			rtnInfo.setSuccess(false);
			rtnInfo.setMessage(e.getMessage());
			rtnInfo.setMessageType(Globals.MESSAGE_TYPE_OPERATION_EXCEPTION);
		} 
		return rtnInfo;
	}
}
