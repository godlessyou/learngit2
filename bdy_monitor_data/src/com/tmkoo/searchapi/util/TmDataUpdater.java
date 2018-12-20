package com.tmkoo.searchapi.util;

import java.util.ArrayList;
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
import com.tmkoo.searchapi.vo.TradeMark;
import com.tmkoo.searchapi.vo.TradeMarkProcess;

@Component
// Spring Service Bean的标识.
//@Transactional
public class TmDataUpdater extends TmDataProcessor {

	// 标志位，当值为true：代表自动更新，即：更新所有符合条件的申请人的商标
	public boolean updateAll=false;

	public SearchUtil searchUtil = new SearchUtil();

	public static String exceptionMsg = "exception-";

	private static String sysMsg1="超过了最大40000次的限制";

	private static String sysMsg2="商标详情接口的查询次数配额不足";

	private static String sysMsg3="api.tmkoo.com";

	private static Logger logger = Logger.getLogger(TmDataUpdater.class);

	private final static String[] zimuList = { "A", "B", "C", "D", "E", "F",
		"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
		"T", "U", "V", "W", "X", "Y", "Z" };


	// 更新数据库中的商标数据
	// 第一步：从数据库中获取商标数据
	// 第二步：按照商标号从API接口获取商标的详细数据，并插入到数据库中
	protected String updateTradeMarkDetail(String regNumber, String tmType,
			Integer updateId) {

		String message = "success";

		String imagePath = Constants.image_dir ;

//		String tempDir = imagePath + "/" + "temp"; //temp用于切割图片时用到，现在不用了
		FileUtil.createFolderIfNotExists(imagePath);

		try {

			int exceptionCount=0;

			boolean finished=true;


			// 检查商标注册号和商标国际分类号是否为空
			if (regNumber == null || regNumber.trim().equals("")
					|| tmType == null || tmType.trim().equals("")) {
				return "注册号为空";
			}

			boolean tmTypeError = false;
			if (tmType != null && !tmType.equals("")) {
				if (!StringUtils.isNum(tmType)) {
					logger.info("商标的国际分类号不是数字");
					tmTypeError = true;
				}
			}
			String status = getStatus(regNumber, tmType);//从监控商标表中获取status
			//查询trademark表中，是否已经存在该注册号和商标类型的数据
			TradeMark sameTradeMark = getTradeMark(regNumber, tmType,null);

			JsonTmInfo jsonTmInfo = null;

			try {

				if (updateAll){
					int remainInfoCount=searchUtil.getRemainInfoCount();
					// 自动更新申请人商标时，保留1000次调用申请人商标详情接口的机会给日常的更新使用
					if (remainInfoCount < 1001){
						finished=false;
						return "次数超出";
					}
				}

				jsonTmInfo = getTmDetail(regNumber, tmType, searchUtil);
			} catch (Exception ex) {
				exceptionCount++;

				String errMsg = ex.getMessage();
				if (errMsg.indexOf("标库网目前还不存在注册号") > -1) {
					logger.info(errMsg);
					//						continue;
				} else if (errMsg.indexOf(sysMsg1) > -1 || errMsg.indexOf(sysMsg2) > -1) {
					throw ex;
				} else if (errMsg.indexOf(sysMsg3) > -1) {
					if (exceptionCount>10){
						throw ex;
					}else{
						ex.printStackTrace();
						return "更新失败";
					}
				} else{
					ex.printStackTrace();
					return "更新失败";
				}
			}
			if (jsonTmInfo == null) {
				for (String zimu : zimuList) {
					// 例如：17602218A
					if (regNumber.endsWith(zimu)) {
						String partRegNumber = regNumber.substring(0,
								regNumber.length() - 1);
						try {
							jsonTmInfo = getTmDetail(partRegNumber, tmType,
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
				return "标库接口中，没有查询到数据";
			}


			// 将查询结果保存到TradeMark对象
			TradeMark tm = new TradeMark();

			// 商标注册号
			tm.setRegnumber(regNumber);

			if (tmTypeError) {
				tmType = jsonTmInfo.getIntCls();
			}
			// 商标国际分类号
			tm.setTmtype(tmType);

			// 代理人
			tm.setAgent(jsonTmInfo.getAgent());

			// 申请人
			String applicantCn = jsonTmInfo.getApplicantCn();
			tm.setApplicantname(applicantCn);

			// 商标文件名
			tm.setImgFileName(jsonTmInfo.getTmImg());

			// 初审公告日期
			String approvalDate = jsonTmInfo.getAnnouncementDate();
			if (approvalDate != null && !approvalDate.equals("")) {
				Date date = DateTool.StringToDate(approvalDate);
				tm.setApprovaldate(date);
			}

			// 初审公告期号
			String approvalNumber = jsonTmInfo.getAnnouncementIssue();
			tm.setApprovalnumber(approvalNumber);

			// 申请日期
			String appDate = jsonTmInfo.getAppDate();
			if (appDate != null && !appDate.equals("")) {
				Date date = DateTool.StringToDate(appDate);
				tm.setAppdate(date);
			}

			// 注册公告日期
			String regDate = jsonTmInfo.getRegDate();
			if (regDate != null && !regDate.equals("")) {
				Date date = DateTool.StringToDate(regDate);
				tm.setRegnoticedate(date);
			}

			// 注册公告期号
			tm.setRegnoticenumber(jsonTmInfo.getRegIssue());

			// 商标名称
			String tmName = jsonTmInfo.getTmName();
			tm.setTmname(tmName);

			// 申请人地址
			tm.setApplicantaddress(jsonTmInfo.getAddressCn());

			// 申请人英文名称
			tm.setApplicantenname(jsonTmInfo.getApplicantEn());

			// 申请人英文地址
			tm.setApplicantenaddress(jsonTmInfo.getAddressEn());

			// 共同申请人
			tm.setGtapplicantname(jsonTmInfo.getApplicantOther1());

			// 商标类型，通常该值为 一般
			tm.setClassify(jsonTmInfo.getCategory());

			// 商标专有权期限
			processPrivateDate(jsonTmInfo, tm);



			Integer tmId=selectIdByRegNumberAndType(regNumber,tmType);
			tm.setId(tmId);

			// 设置商标流程
			List<TradeMarkProcess> tmPs = processTmFlow(jsonTmInfo);


			// 更新数据库中的商品/服务数据
			updateTmGoods(jsonTmInfo, tm, regNumber, tmType);

			// 更新数据库中的商标流程数据
			if (tmPs != null && tmPs.size() > 0) {
				tm.setId(tmId);
				if (tmId ==null) {
					logger.info("can not get tmid from trademark table ");
					// System.exit(1);
				} else {
					for (TradeMarkProcess tmProcess : tmPs) {
						tmProcess.setTmid(tmId);
					}
					updateTmProcessDate(tmId, tmPs);
				}
			}

			// 如果有公告数据，可以根据公告数据设置商标状态
			boolean statusSet = getTmStatusFromGongGao(regNumber,
					tm);

			// 如果无法通过公告设置商标状态，从商标流程中获取商标状态
			if (!statusSet) {
				// 按照流程信息的日期降序排列，将最新的流程信息排在前面
				UseComparator.sort(tmPs, "statusDate", "desc");
				
				statusSet = getTmStatusFromLiucheng(tmPs, tm);
			}

			// 如果无法从商标流程中获取商标状态，那么使用接口获取的status
			if (!statusSet) {
				if (status != null && !status.equals("")) {
					tm.setStatus(status);
				}
			}
			// 如果本地不存在图片，或者图片内容为“暂无图”，需要更新商标的图片
			boolean needUpdateImage = ifNeedUpdateImage(regNumber, imagePath);
			if (!needUpdateImage) {
				// 如果数据库中商标的图片为null，需要更新商标的图片
				boolean imageIsNull = tmImageIsNull(regNumber, tmType);
				if (imageIsNull) {
					needUpdateImage = true;
				}
			}
			String photo = null;
			String imgFileName=null;
			if (needUpdateImage) {
				if (imgFileName == null || imgFileName.equals("")) {
					imgFileName = jsonTmInfo.getTmImg();
				}
				try {
					// 处理商标图片，如果本地没有图片，将从API接口获取
					photo = processTmImage(imgFileName, regNumber,
							imagePath);
					if (photo != null) {
						tm.setTmimage(photo);
					} else {
						needUpdateImage = false;
					}
				} catch (Exception e) {
					needUpdateImage = false;
					e.printStackTrace();
				}
			}
			//tm.set
			// 如果本次从接口获取的商标详细数据与数据库中的不同，执行商标数据的更新
			if(sameTradeMark==null){
				try {
					insertTradeMarkTable(tm);
					if(tmId==null){
						tmId=selectIdByRegNumberAndType(regNumber,tmType);
						for (TradeMarkProcess tmProcess : tmPs) {
							tmProcess.setTmid(tmId);
						}
						updateTmProcessDate(tmId, tmPs);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(!compareString(sameTradeMark, tm)) {
					try {
						updateTradeMarkTable(tm,needUpdateImage);
					} catch (Exception e) {
						e.printStackTrace();
					}
			} else {
				
				// 只需要更新商标图片
				if (needUpdateImage) {
					System.out.println("商标相同更新,只更新图片");
					updateTradeMarkImage(tmId, photo);
				}else{
					System.out.println("商标相同更新,只更新日期");
					// 更新本次同步数据的日期
					updateTradeMarkModifyData(tmId);
				}
			}
//			insertTradeMarkTable(tm);

		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
			return exceptionMsg + message;
		}

		logger.info("申请号： " + regNumber + "，商品类型："+tmType+"的商标数据已经更新");

		message = "申请号： " + regNumber + "，商品类型："+tmType+"的商标数据已经更新";
		return message;
	}
	// 只更新数据库中的商标公告
	protected String updateGongGao(Map<String, String> regNumberAndTmType) {
		String message = "success";
		// 设置API Key
		setKey();

		int total = 0;
		try {

			List<String> regNumberList = new ArrayList<String>();
			for(Map.Entry<String, String> entry :regNumberAndTmType.entrySet()){
				regNumberList.add(entry.getKey());
			}

			for (String regNumber : regNumberList) {				

				if (regNumber == null || regNumber.equals("")) {
					continue;
				}

				if (updateAll){
					int remaiGgCount=searchUtil.getRemainGgCount();
					// 自动更新，保留1000次调用申请人商标公告接口的机会给日常的更新使用
					if (remaiGgCount < 1001){
						break;
					}
				}

				// 根据商标注册号，从API接口获取商标公告
				JsonTmGongGaoInfo jsonTmGongGaoInfo = getGongGao(regNumber,searchUtil);

				if (jsonTmGongGaoInfo != null) {
					int size = jsonTmGongGaoInfo.getGonggaos().size();
					// 如果没有获取到数据，jsonTmGongGaoInfo的custId属性值为null
					if (size == 0) {
						continue;
					}
					// 处理商标的公告数据
					int count = processGongGaoDate(regNumber,
							jsonTmGongGaoInfo);
					total = total + count;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
			return exceptionMsg + message;

		}

		logger.info( total + " 条商标公告数据已经保存到数据库");

		message = total + " 条商标公告数据已经保存到数据库";

		return message;
	}
	public static void main(String[] args) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
