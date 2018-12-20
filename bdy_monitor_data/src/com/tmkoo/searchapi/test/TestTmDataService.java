package com.tmkoo.searchapi.test;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;


//import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tmkoo.searchapi.util.GongGaoTmDataService;




@RunWith(SpringJUnit4ClassRunner.class)
// 表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class TestTmDataService {
//	private static Logger logger = Logger.getLogger(TestTmDataService.class);

	
	@Resource
	private GongGaoTmDataService tmDataService;
	
	
	
	@Test
	public void testService() {
		
//		List<String> custNames=new ArrayList<String>();
		
//		custNames.add("北京外语教学与研究出版社有限责任公司");
		
//		custNames.add("红蜻蜓集团有限公司");
		
		
		
		// custNames=tmDataService.getCustNames();
		
		String custName="咪咕文化科技有限公司";
		
		try {
//			tmDataService.updateTmDataByThread(null, custName, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

}