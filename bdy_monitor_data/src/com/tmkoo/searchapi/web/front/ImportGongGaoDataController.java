package com.tmkoo.searchapi.web.front;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tmkoo.searchapi.common.ReturnInfoNoTotal;
import com.tmkoo.searchapi.util.MonitorGgDataService;

@Controller
@RequestMapping(value = "/interface/gonggao")
public class ImportGongGaoDataController {
	
	@Resource
	public MonitorGgDataService monitorGgDataService;
	
	@RequestMapping(value = "/importdata", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public ReturnInfoNoTotal importGgData(String ggPath,Integer ggQihao) {
		return monitorGgDataService.upateGongGao(ggPath,ggQihao);
	}
}
