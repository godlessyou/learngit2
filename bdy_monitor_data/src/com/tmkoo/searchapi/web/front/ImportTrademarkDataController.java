package com.tmkoo.searchapi.web.front;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tmkoo.searchapi.common.Global;
import com.tmkoo.searchapi.common.ReturnInfo;
import com.tmkoo.searchapi.common.ReturnInfoNoTotal;
import com.tmkoo.searchapi.util.MonitorGgDataService;
import com.tmkoo.searchapi.util.SearchUtil;
import com.tmkoo.searchapi.util.TmDataService;
import com.tmkoo.searchapi.vo.JsonTmInfoList;

@Controller
@RequestMapping(value = "/interface/trademark")
public class ImportTrademarkDataController {
	
	@Resource
	public TmDataService tmDataService;
	
	@RequestMapping(value = "/importdata", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public ReturnInfoNoTotal importTrademarkData() {
		ReturnInfoNoTotal returnInfo = new ReturnInfoNoTotal();
		String msg = tmDataService.updateAllTmData();
		returnInfo.setMessage(msg);
		returnInfo.setSuccess(true);
		return returnInfo;
	}
}
