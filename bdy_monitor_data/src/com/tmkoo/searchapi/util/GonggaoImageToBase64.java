package com.tmkoo.searchapi.util;

import java.util.List;

import com.tmkoo.searchapi.vo.MonitorGongGao;

public class GonggaoImageToBase64 {
	public static void main(String[] args) throws Exception {
		//取公告的路径集合
		//转换为base64
		//插入数据库
		List<MonitorGongGao> ggs = MonitorGgDbService.getImagePath();
		GongGaoTmDataProcessor processor = new GongGaoTmDataProcessor();
		for(MonitorGongGao gg : ggs){
//			System.out.println(gg.getId()+":"+gg.getImagePath());
			String photo = processor.getBase64Image(gg.getImagePath());
			gg.setTmImage(photo);
			MonitorGgDbService.updateTmImage(gg);
		}
	}
}
