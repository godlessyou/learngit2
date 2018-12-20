package com.tmkoo.searchapi.util;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tmkoo.searchapi.common.Constants;
import com.tmkoo.searchapi.vo.JsonTmGongGaoInfo;
import com.tmkoo.searchapi.vo.JsonTmInfo;
import com.tmkoo.searchapi.vo.JsonTmInfoList;
import com.tmkoo.searchapi.vo.MonitorGongGao;
import com.tmkoo.searchapi.vo.TradeMark;
import com.tmkoo.searchapi.vo.TradeMarkProcess;

@Component
// Spring Service Bean的标识.
@Transactional
public class GongGaoTmDataUpdater extends GongGaoTmDataProcessor {

	public static String exceptionMsg = "exception-";
	
	private static String sysMsg1="超过了最大40000次的限制";
	
	private static String sysMsg2="商标详情接口的查询次数配额不足";
	
	private static String sysMsg3="api.tmkoo.com";
	

	private static Logger logger = Logger.getLogger(GongGaoTmDataUpdater.class);

	private final static String[] zimuList = { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
			"T", "U", "V", "W", "X", "Y", "Z" };
	

	

	// 更新数据库中的商标数据
	// 第一步：从数据库中获取商标数据
	// 第二步：按照商标号从API接口获取商标的详细数据，并插入到数据库中
	protected String updateTradeMarkDetail(
			List<MonitorGongGao> ggList) throws Exception {

		String message = "success";

		String opt = Constants.tmOpt;

		int total = 0;

		String baseImagePath = Constants.image_dir ;
				
		String imagePath = null ;

		SearchUtil searchUtil = new SearchUtil();
		
		String appName=null;
		
		TmDataGetor tmDataGetor=new TmDataGetor();
		
		MonitorGgDbService monitorGgDbService=new MonitorGgDbService();

//		try {
			
			int exceptionCount=0;
			
			int imageSubDir=0;

			for (MonitorGongGao tradeMark : ggList) {
				// 商标注册号
				String regNumber = tradeMark.getRegNumber();
				
				// 商标国际分类号
				String tmType = tradeMark.getTmType();
				
				Integer tmId = tradeMark.getId();
				
				int id=tmId.intValue();				
				
				int subDir=id/12000;
				
				subDir=subDir+1;
				
				if (subDir>imageSubDir){
					imageSubDir=subDir;
					// 创建保存图片的目录
					imagePath=baseImagePath+"/"+imageSubDir;					
					FileUtil.createFolderIfNotExists(imagePath);
					
				}
				// 检查商标注册号和商标国际分类号是否为空
				if (regNumber == null || regNumber.trim().equals("")
						|| tmType == null || tmType.trim().equals("")) {
					continue;
				}

				boolean tmTypeError = false;
				if (tmType != null && !tmType.equals("")) {
					//如果该商标有多个国际分类号，取第一个即可。
					int pos=tmType.indexOf(";");
					if (pos>-1){
						tmType=tmType.substring(0,pos);
						tmType=tmType.trim();
					}
					if (!StringUtils.isNumeric(tmType)) {
						logger.info("商标的国际分类号不是数字");
						tmTypeError = true;
					}
				}

				JsonTmInfo jsonTmInfo = null;

				try {
					
					int remainInfoCount=searchUtil.getRemainInfoCount();
					// 自动更新申请人商标时，保留1000次调用申请人商标详情接口的机会给日常的更新使用
					if (remainInfoCount < 1001){							
						break;
					}
					
					jsonTmInfo = tmDataGetor.getTmDetail(regNumber, tmType, searchUtil);
				} catch (Exception ex) {
					
					exceptionCount++;
					
					String errMsg = ex.getMessage();
					if (errMsg.indexOf("标库网目前还不存在注册号") > -1) {
						logger.info(errMsg);						
					}else if (errMsg.indexOf(sysMsg1) > -1 || errMsg.indexOf(sysMsg2) > -1) {
						throw ex;
					}else if (errMsg.indexOf(sysMsg3) > -1) {
						if (exceptionCount>10){
							throw ex;
						}else{
							ex.printStackTrace();
							continue;
						}
					}else{
						ex.printStackTrace();
						continue;
					}
				}

				if (jsonTmInfo == null) {
					for (String zimu : zimuList) {
						// 例如：17602218A
						if (regNumber.endsWith(zimu)) {
							String partRegNumber = regNumber.substring(0,
									regNumber.length() - 1);
							try {
								jsonTmInfo = tmDataGetor.getTmDetail(partRegNumber, tmType,
										searchUtil);
							} catch (Exception ex) {
								exceptionCount++;
								
								String errMsg = ex.getMessage();
								if (errMsg.indexOf("标库网目前还不存在注册号") > -1) {
									logger.info(errMsg);
									continue;
								} else if (errMsg.indexOf(sysMsg1) > -1 || errMsg.indexOf(sysMsg2) > -1) {
									throw ex;
								} else if (errMsg.indexOf(sysMsg3) > -1) {
									if (exceptionCount>10){
										throw ex;
									}else{
										ex.printStackTrace();
										continue;
									}
								} else{
									ex.printStackTrace();
									continue;
								}
							}
							break;
						}
					}
				}

				if (jsonTmInfo == null) {
					continue;
				}

				// 将查询结果保存到TradeMark对象
				MonitorGongGao tm = new MonitorGongGao();

				// 设置商标的id
				tm.setId(tmId);		
			

				if (tmTypeError) {
					tmType = jsonTmInfo.getIntCls();
					// 商标国际分类号
					tm.setTmType(tmType);
				}

				// 代理人
				tm.setAgent(jsonTmInfo.getAgent());

				// 申请人
				String applicantCn = jsonTmInfo.getApplicantCn();

				processAppName(applicantCn, tm);
				
				appName=applicantCn;				

				// 商标名称
				String tmName = jsonTmInfo.getTmName();
				
				// logger.info("tmName: " + tmName);
				if (applicantCn != null && applicantCn.equals("北京君策九州科技有限公司")) {
					if (tmName == null) {
						continue;
					} else {
						if (!tmName.equals("慧管佳") && !tmName.equals("IPGO")
								&& !tmName.equals("知产狗")) {
							continue;
						}
					}
				}				
				tm.setTmNameCn(tmName);
				
				// 0：商标名称是文字，代表文字商标
				// 1：商标名称叫做图形，代表图形商标
				int monitorType=0;				
				if (tmName.equals("图形")){
					monitorType=1;					
				}				
				tm.setMonitorType(monitorType);
				

				// 更新数据库中的商品/服务数据
				updateTmGoods(jsonTmInfo, tm, tmType);
								
				// 商标文件链接
				String imageLink = jsonTmInfo.getTmImg();
				tm.setImageLink(imageLink);	
					
				// 处理商标图片，如果本地没有图片，将从API接口获取
				String fileName = regNumber + ".jpg";
				String imageFile = imagePath + "/" + fileName;
			
			    String photo= processTmImage(imageLink,imageFile);
			   
			    tm.setImagePath(imageFile);
			    tm.setTmImage(photo);

				// 执行商标数据的更新				
				try {
					monitorGgDbService.updateMonitorGongGao(tm);
					total++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			message = e.getMessage();
//			return exceptionMsg + message;
//		}
		logger.info("申请人： " + appName + "的 " + total + " 条商标数据已经更新");

		message = "申请人： " + appName + "的 " + total + " 条商标数据已经更新";
		return message;
	}
	public static void main(String[] args) {
		try {

			GongGaoTmDataUpdater tmDataUpdater = new GongGaoTmDataUpdater();

			String custIds = args[0];
			String appName = args[1];

			Integer updateId = 0;

			Integer custId = new Integer(custIds);

			// 设置API Key
			tmDataUpdater.setKey();

//			List<TradeMark> tmList = tmDataUpdater.getCustomerTmList(custId);

//			tmDataUpdater.updateGongGao(custId, appName);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
