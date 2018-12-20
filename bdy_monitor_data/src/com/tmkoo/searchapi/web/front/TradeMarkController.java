package com.tmkoo.searchapi.web.front;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tmkoo.searchapi.common.Constants;
import com.tmkoo.searchapi.common.Global;
import com.tmkoo.searchapi.util.AuthenticationService;
import com.tmkoo.searchapi.util.ServiceUrlConfig;


/**
 * 搜索
 * 
 * @author tmkoo
 */
@Controller
@RequestMapping(value = "/trademark")
public class TradeMarkController {


	@Resource
	private ServiceUrlConfig serviceUrlConfig;
	
	@Resource
	private AuthenticationService authenticationService;
	
	/**
	 * 搜索初始化，进入过渡页，给用户提示等待5秒。
	 * @param searchType
	 * @param searchKey
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST )
	public String list(@RequestParam("custName") String custName, 
			@RequestParam(value = "appName") String appName,
			Model model,
			ServletRequest request) {
		 
		model.addAttribute("custName", custName);
		model.addAttribute("appName", appName);  

		return "trademark/loading";
	}
	
	

	
	
 
	/**
	 * Ajax真正的搜索，返回搜索结果编号，将搜索结果存入SESSION。
	 * @param searchType
	 * @param searchKey
	 * @return
	 */
	@RequestMapping(value = "dosearch")
	@ResponseBody
	public String dosearch(@RequestParam("custName") String custName, 
			@RequestParam(value = "appName") String appName,
			HttpServletRequest request
	) {		

		
		if(Global.webProperties.API_KEY==null || Global.webProperties.API_KEY.trim().equals("")){
			return "NOTSET";//管理员还未配置API呢
		}
	
		
		boolean updateAllApp=false;
		List<String> appNames=new ArrayList<String>();
		if (appName != null && !appName.equals("")) {	
			if(appName.equalsIgnoreCase("all")){
				updateAllApp=true;
			}else{
				StringTokenizer tok = new StringTokenizer(appName, "\r\n");
				while (tok.hasMoreTokens()) {
					String key = tok.nextToken();
					appNames.add(key);
				}
			}
		}
		
		String opt=Constants.tmAndggOpt;
		String message= null;
		
		
		
		String msg=message;
//		String success="success-";		
//		String exception="exception-";
//		if (message!=null){
//			if (message.indexOf(success)>-1){
//				int len=success.length();
//				int pos=message.indexOf(success);
//				 msg=message.substring(pos+len);			
//			}else if(message.indexOf(exception)>-1){				
//				int len=exception.length();
//				int pos=message.indexOf(exception);
//				msg=message.substring(pos+len);				
//			}
//		}
		
		return "SEARCHRESULT:"+msg; 
	}
	
	/**
	 * 翻页，从session中读取原来的列表
	 * @param pageNo
	 * @param l
	 * @param intClss
	 * @param intCls
	 * @param searchType
	 * @param searchKey
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "result", method = RequestMethod.GET)
	public String result(@RequestParam(value = "result", defaultValue = "1") String result, //操作结果
			@RequestParam(value = "message", defaultValue = "") String message,  //消息		
			Model model,
			HttpServletRequest request) {    	
		
		//将这些数据返回到页面
		model.addAttribute("result", result);
		model.addAttribute("message", message);
		
		return "trademark/result";
	}
	
	/**进入未登录页面***/
	@RequestMapping(value = "unlogin", method = RequestMethod.GET)
	public String unlogin() { 
		return "trademark/unlogin";
	}
	@RequestMapping(value = "noresult", method = RequestMethod.GET)
	public String noresult() { 
		return "trademark/noresult";
	}
	/**进入机器人提示页面***/
	@RequestMapping(value = "rabot", method = RequestMethod.GET)
	public String rabot() { 
		return "trademark/rabot";
	}
	

	
	
}
