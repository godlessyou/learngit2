package com.tmkoo.searchapi.web.front;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tmkoo.searchapi.common.Global;
import com.tmkoo.searchapi.common.ReturnInfoNoTotal;
import com.tmkoo.searchapi.util.SearchUtil;
import com.tmkoo.searchapi.vo.JsonTmInfoList;

@Controller
@RequestMapping(value = "/interface")
public class TmkooApiSearchController {
	@RequestMapping(value = "/searchapi", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public ReturnInfoNoTotal search(String number,String keyword) {
		ReturnInfoNoTotal returnInfo = new ReturnInfoNoTotal();
		SearchUtil searchUtil = new SearchUtil();
		searchUtil.init();
		if (Global.webProperties.API_KEY==null || !Global.webProperties.API_KEY.equals("QIJIAN_380109332")){
			Global.webProperties.API_KEY = "QIJIAN_380109332";
			Global.webProperties.API_PASSWORD = "SMQicSvJNB";
		}
		try {
			List<JsonTmInfoList> jsonTmInfoLists = searchUtil.search(number, keyword);
			returnInfo.setSuccess(true);
			returnInfo.setData(jsonTmInfoLists);
			returnInfo.setMessage("查询商标成功");
		} catch (Exception e) {
			e.printStackTrace();
			returnInfo.setSuccess(false);
			returnInfo.setMessage(e.getMessage());
		}
		return returnInfo;
	}
}
