package com.tmkoo.searchapi.util;


import java.util.List;


import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tmkoo.searchapi.vo.JsonSbGongGao;
import com.tmkoo.searchapi.vo.JsonTmGongGaoInfo;
import com.tmkoo.searchapi.vo.JsonTmInfo;
import com.tmkoo.searchapi.vo.JsonTmInfoList;



@Component
// Spring Service Bean的标识.
@Transactional
public class TmDataGetor  extends TmDbService {


	private static Logger logger = Logger.getLogger(TmDataGetor.class);
	
	// 从API接口获取商标详细数据
	public JsonTmInfo getTmDetail(String regNumber, String tmType,
			SearchUtil searchUtil) throws Exception{

		JsonTmInfo jsonTmInfo = null;
		int tryTime = 3;
//		while (tryTime > 0) {

			// logger.info("按照注册号：" + regNumber + "，从API接口获取商标详细数据");

			long start2 = System.currentTimeMillis();

			jsonTmInfo = searchUtil.getTmDetailInfo(regNumber, tmType);

			long end2 = System.currentTimeMillis();

			long duration2 = (end2 - start2) / 1000;

			logger.info("按照注册号：" + regNumber + "，获取商标详细数据耗时：" + duration2
					+ " 秒");

//			if (jsonTmInfo != null) {
//				break;
//			} else {
//				tryTime--;
//			}
//		}

		return jsonTmInfo;
	}
	// 从API接口获取商标公告数据
		protected JsonTmGongGaoInfo getGongGao(String regNumber,SearchUtil searchUtil) throws Exception{

			JsonTmGongGaoInfo jsonTmGongGaoInfo = null;
			int tryTime = 1;
			while (tryTime > 0) {

				// logger.info("按照注册号：" + regNumber + "，从API接口获取商标公告数据");

				long start = System.currentTimeMillis();

				jsonTmGongGaoInfo = searchUtil.getTmGongGaoInfo(regNumber);

				long end = System.currentTimeMillis();

				long duration = (end - start) / 1000;

				logger.info("按照注册号：" + regNumber + "，获取" + regNumber + "的商标公告耗时："
						+ duration + " 秒");

				if (jsonTmGongGaoInfo != null) {
					List<JsonSbGongGao> list = jsonTmGongGaoInfo.getGonggaos();
					if (list != null && list.size() > 0) {
//						jsonTmGongGaoInfo.setCustId(custId);
						jsonTmGongGaoInfo.setRegNumber(regNumber);
					}
					break;
				} else {
					tryTime--;
				}
			}

			return jsonTmGongGaoInfo;
		}

	

	
}
