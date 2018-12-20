package com.tmkoo.searchapi.util;

import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.tmkoo.searchapi.vo.MonitorGongGao;

@Component
// Spring Service Bean的标识.
@Transactional

public class GongGaoTmDataService extends GongGaoTmDataUpdater {
	
	@Resource
	private ServiceUrlConfig serviceUrlConfig;
	
    //重复尝试的次数
	private int limitCount=3;
	private static Logger logger = Logger.getLogger(GongGaoTmDataService.class);
	public void updateTmDetail(List<MonitorGongGao> ggList) throws Exception{
		//设置API Key
		setKey();
		// 更新商标详细数据		
		updateTradeMarkDetail(ggList);					
	}
	public static void main(String[] args) {
		try {
			GongGaoTmDataService tmDataService = new GongGaoTmDataService();
			String custName2="一汽大众汽车有限公司";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



