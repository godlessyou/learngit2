package com.tmkoo.searchapi.util;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.tmkoo.searchapi.common.Constants;
import com.tmkoo.searchapi.vo.TradeMark;



@Component
// Spring Service Bean的标识.
//@Transactional

public class TmDataService extends TmDataUpdater {
	
	@Resource
	private ServiceUrlConfig serviceUrlConfig;
	
	@Resource
	private UpdateConfig updateConfig;
	
    //重复尝试的次数
	private int limitCount=1;
	
	private final ExecutorProcessPool pool = ExecutorProcessPool.getInstance();
	
	private static Logger logger = Logger.getLogger(TmDataService.class);
		
	// 更新商标数据
	private String insertTradeMark(Map<String, String> regNumberAndType, StringBuilder message, StringBuilder errMessage) throws Exception {
		
		if (updateConfig==null){
			updateConfig= new UpdateConfig();
		}
		String	interval=updateConfig.getTm_interval();
		for(Map.Entry<String, String> ent : regNumberAndType.entrySet()){
			
			String regNumber=ent.getKey();
			String tmType=ent.getValue();
			//查询trademark表中，是否已经存在该注册号和商标类型的数据
			TradeMark sameTradeMark = getTradeMark(regNumber, tmType,interval);
			//已经存在该商标的详细信息,且更新的时间小于5天，不需要更新
			if(sameTradeMark!=null){
//				message.append("已经存在该商标，且更新日期小于5天");
				System.out.println("注册号："+regNumber+",商品类型："+tmType+",已存在该商标，且更新日期小于5天，不需要更新");
				continue;
			}else{
				insertTmDetail(regNumber,tmType, message, errMessage);
			}
			
		}
		String result = message + "<tr></tr>" + errMessage;
        return result;		

	}
	
	private boolean insertTmDetail(String regNumber, String tmType, StringBuilder message, StringBuilder errMessage) throws Exception {
		String msg=null;		
		Integer updateId=null;
		// 更新商标详细数据		
		msg = updateTradeMarkDetail(regNumber, tmType, updateId);					
		processMsg(message,errMessage,msg);	
		if(msg.indexOf("exception")>-1){
			return false;
		}

		return true;
	}
	// 按照注册号和类别查找商标数据/商标公告数据，并插入数据库
	private String updateData(Map<String, String> regNumberAndType,String opt) {
		//设置API Key
		setKey();		
		
		StringBuilder message = new StringBuilder();
		StringBuilder errMessage = new StringBuilder();
		String result="";
		try {
			if(opt==null){
				return "选择更新商标或公告";
			}else if(opt.equals(Constants.tmOpt)){
				//插入商标数据		
				result = insertTradeMark(regNumberAndType, message, errMessage);
			}else if(opt.equals(Constants.gonggaoOpt)){
				String msg =updateGongGao(regNumberAndType);
				processMsg(message,errMessage, msg);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String msg=e.getMessage();
			if (errMessage.equals("")) {
				errMessage = errMessage.append("exception-" + msg);
			}else{
				errMessage = errMessage.append("<p></p>" + msg);
			}				
		}		
		result = message + "<tr></tr>" + errMessage;
		return result;
	}
	
	
	// 更新系统中所有被检测商标的商标数据
	public String updateAllTmData() {
		//设置API Key
		setKey();
		searchUtil.init();
//		String opt=Constants.gonggaoOpt;
		String opt=Constants.tmOpt;
		String message = "";		
		
		Map<String, String> regNumberAndType=null;
		try {
			regNumberAndType = getRegNumberAndType();
//			regNumberAndType=new HashMap<String, String>();
//			regNumberAndType.put("21014218", "12");
		} catch (Exception e1) {
			e1.printStackTrace();
		}	
		if (regNumberAndType==null){
			message = "系统中不存在被监测商标，请系统管理员先在系统中添加监测商标";
			return exceptionMsg + message;
		}
		updateAll=true;
		//更新商标数据	
		String msg=updateData(regNumberAndType,opt);	
		message=message+msg;
		return message;
	}
	public static void main(String[] args) {
		try {
			TmDataService tmDataService = new TmDataService();
			String msg = tmDataService.updateAllTmData();
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
}



