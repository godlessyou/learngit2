package com.tmkoo.searchapi.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;




import java.util.StringTokenizer;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tmkoo.searchapi.common.Constants;
import com.tmkoo.searchapi.common.Global;
import com.tmkoo.searchapi.vo.JsonSbGongGao;
import com.tmkoo.searchapi.vo.JsonSblc;
import com.tmkoo.searchapi.vo.JsonSysp;
import com.tmkoo.searchapi.vo.JsonTmGongGaoInfo;
import com.tmkoo.searchapi.vo.JsonTmInfo;
import com.tmkoo.searchapi.vo.MonitorGongGao;
import com.tmkoo.searchapi.vo.TradeMark;
import com.tmkoo.searchapi.vo.TradeMarkCategory;
import com.tmkoo.searchapi.vo.TradeMarkProcess;

@Component
// Spring Service Bean的标识.
@Transactional
public class GongGaoTmDataProcessor {
	@Resource
	private UpdateConfig updateConfig;

	private static Logger logger = Logger.getLogger(GongGaoTmDataProcessor.class);
	
	
	// 获取图片
	protected String processTmImage(String imageLink,
			String imageFile) throws Exception{
		
		if(imageLink!=null && !imageLink.equals("")){
			getTmImage(imageLink,imageFile);
		}
		String photo = getBase64Image(imageFile);
		return photo;
	}
	
	
	
	// 从API接口获取图片，并保存到本地
	protected void getTmImage(String imageLink,
			String imageFile) throws Exception{	
		
		File localFile = new File(imageFile);
		if (!localFile.exists()) {		
			/*
		      tmImg:"121b99cebccde101", //商标图片名
		              需要小图(50*50)，http://tmpic.tmkoo.com/121b99cebccde101-s
	                        需要中图(100*100)，http://tmpic.tmkoo.com/121b99cebccde101-m
	                        需要大图，http://tmpic.tmkoo.com/121b99cebccde101
	          */
			String url = "http://tmpic.tmkoo.com/" + imageLink+"-m";
			ImageUtils.getImageFromUrl(url, imageFile);
		}
		
		
	}
	
	// 处理商标图片
	protected String getBase64Image(String imageFile) throws Exception{
		String photo = null;
		File file2 = new File(imageFile);
		if (!file2.exists()) {
			logger.info("商标图样不存在，用暂无图替代！+ file path: "
					+ imageFile);
			String base64Image =Constants.zanwutu;
			photo = "data:image/png;base64," + base64Image;
		} else {
			String base64Image = ImageUtils.encodeImgageToBase64(imageFile);
			photo = "data:image/png;base64," + base64Image;
		}
		return photo;
	}

	// 更新商品/服务数据
	protected void updateTmGoods(JsonTmInfo jsonTmInfo,
			MonitorGongGao tm, String tmType)
			throws Exception {

	
		String tmGroup = "";
		// 商品和服务
		List<JsonSysp> goods = jsonTmInfo.getGoods();
		List<String> tmGroupList=new ArrayList<String>();
		for (JsonSysp service : goods) {			
			String group = service.getGoodsCode();
			if (!tmGroupList.contains(group)){
				tmGroupList.add(group);
			}
		}
		
		if (tmGroupList.size()>0){		
			//排序
			ListCompare.sort(tmGroupList);			
			
			for (String group:tmGroupList ){		
				if (tmGroup.equals("")) {
					tmGroup = tmGroup + group;
				} else {
					tmGroup = tmGroup + ";" + group;
				}	
			}
		}
		tm.setTmGroup(tmGroup);
	
	
	}
	
	
	// 处理商标申请人
	protected void processAppName(String applicantCn,MonitorGongGao tm) throws Exception {
		
		if (applicantCn!=null && !applicantCn.equals("")){	
			String appName=applicantCn;
			int pos=applicantCn.indexOf(";");					
			if(pos>-1){
				appName=applicantCn.substring(0,pos);
			}
			
			tm.setAppName(appName);
			
		}
	}
		



	// 处理命令行参数
	protected List<String> processParameter(String[] args) {

		int len = args.length;
		List<String> appNames = new ArrayList<String>();
		for (int i = 2; i < len; i++) {
			String s = args[i];
			if (s.indexOf("#") > -1) {
				s = s.replaceAll("#", " ");
			}
			appNames.add(s);

		}
		return appNames;
	}

	
	
    protected void setKey(){
    	if (Global.webProperties.API_KEY==null || !Global.webProperties.API_KEY.equals("QIJIAN_380109332")){
			Global.webProperties.API_KEY = "QIJIAN_380109332";
			Global.webProperties.API_PASSWORD = "SMQicSvJNB";
		}
    }
    

	
}
