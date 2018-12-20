package com.tmkoo.searchapi.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import com.tmkoo.searchapi.common.Constants;
import com.tmkoo.searchapi.mail.MailSender;
import com.tmkoo.searchapi.vo.GongGao;
import com.tmkoo.searchapi.vo.MonitorGongGao;
import com.tmkoo.searchapi.vo.Users;

public class MonitorGgProcessService extends MonitorGgDbService {

	public static String gongGaoPath = Constants.gonggao_dir;

	// 处理公告，找到需要的公告数据
	public static List<MonitorGongGao> getChuShenGongGao(List<MonitorGongGao> ggList, GongGao gongGao)
			throws Exception {

		Date ggDate = gongGao.getGgDate();
		int ggQihao = gongGao.getGgQihao();
		int ggPage = gongGao.getGgPage();
		int ggPageNo = gongGao.getGgPageNo();
		String ggFilePath = gongGao.getGgFilePath();
		
//		if(ggPageNo==97){
//			System.out.println("2222222222222");
//		}

		
		try {
			
			List<String> ggContents = gongGao.getContents();
			
//			int size=ggContents.size();
//			
//			int yushu=size%2;
			
//			if (yushu>0){
//				System.out.println("regNumber and tmType count is not equal");
//			}

			for (String line : ggContents) {
				line = line.trim();				

				String title="regNumber:";				
				String regNumber= getContent(line, title);
				if (regNumber != null && !regNumber.equals("")) {					
					//判断注册号是否为数字或者英文
					if(StringUtils.isNumericOrEn(regNumber)){					
						MonitorGongGao gg = new MonitorGongGao();
						gg.setRegNumber(regNumber);
						gg.setGgDate(ggDate);
						gg.setGgQihao(ggQihao);
						gg.setGgPage(ggPage);
						gg.setLocationPage(ggPageNo);
						gg.setGgFilePath(ggFilePath);
						ggList.add(gg);
					}
					else{
						System.out.println(regNumber);
					}
					continue;
				}
					
				
			    title="tmType:";				
				String tmType= getContent(line, title);
				if (tmType != null && !tmType.equals("")) {				
					if (StringUtils.isNumeric(tmType)) {
						int ggSize = ggList.size();
						if(ggSize==0){
							System.out.println("regNumber is set");
						}else{
							int index=ggSize-1;							
							MonitorGongGao gg = ggList.get(index);
							String typeStr=gg.getTmType();
							if (typeStr==null || typeStr.equals("")){
								typeStr = tmType;
							}else{
								typeStr = typeStr + ";" + tmType;
							}							
							gg.setTmType(typeStr);	
						}
					}else{
						System.out.println(tmType);
					}
					continue;
				}				

			}

		} catch (Exception e) {
			throw e;
		}

		return ggList;
	}
	
	
	private static String getContent(String line, String title){
		String result= null;			
		int pos1=line.indexOf(title);	
		if (pos1 > -1) {	
			int len=title.length();
			line=line.substring(pos1+len);
			result=line.trim();			
		}		
		return result;
		
	}

	
	
	// 处理公告，找到需要的公告数据
	public static List<MonitorGongGao> getGongGaoData(List<GongGao> gonggaoList)
			throws Exception {
		List<MonitorGongGao> ggList = new ArrayList<MonitorGongGao>();
		try {
			for (GongGao gongGao : gonggaoList) {

				// 针对每一页的公告数据进行处理，获取商标注册号和商标国际分类号
				List<MonitorGongGao> list = getChuShenGongGao(ggList, gongGao);
				if (list == null || list.size() == 0) {
					continue;
				}

				// 将每一页的公告数据累加
//				addGongGaoData(list, ggList);

			}
		} catch (Exception e) {
			throw e;
		}
		return ggList;
	}

	
	
	// 将每一页的公告数据累加
	private static void addGongGaoData(List<MonitorGongGao> list,
			List<MonitorGongGao> ggList) throws Exception {
		for (MonitorGongGao mgg : list) {
			String regNumber = mgg.getRegNumber();
			String tmType = mgg.getTmType();
			
			if (tmType==null || tmType.equals("")){
				continue;
			}

			boolean find = false;
			for (MonitorGongGao gg : ggList) {
				String regNum = gg.getRegNumber();
				String typeStr = gg.getTmType();
				if (typeStr == null || typeStr.equals("")) {
					continue;
				}
				if (regNumber.equals(regNum)) {
					boolean sameOne = false;
					StringTokenizer idtok = new StringTokenizer(typeStr, ";");
					while (idtok.hasMoreTokens()) {
						String value = idtok.nextToken();
						if (value.equals(tmType)) {
							sameOne = true;
							break;
						}
					}
					if (!sameOne) {
						typeStr = typeStr + ";" + tmType;
						gg.setTmType(typeStr);
					}
					find = true;
					break;
				}
				
			}

			if (!find) {
				ggList.add(mgg);
			}
		}

	}

	
	// 对公告文件进行截图，保存到指定目录下。
	private static void getGgPage(String ggFilePath, String imageDir,
			 List<MonitorGongGao> list)
			throws IOException {

		List<String> imageList = new ArrayList<String>();
		List<Integer> pageNoList = new ArrayList<Integer>();

		boolean splitPdf = false;
		for (MonitorGongGao gg : list) {

			int ggQihao = gg.getGgQihao();

			// 在所有当期公告（一般都是多个pdf）中的总的页号
			int ggPage = gg.getGgPage();

			// 在当前这个pdf文档中位于第几页
			int ggPageNo = gg.getLocationPage();
			
			String ggType = "商标初步审定公告";

			String fileName = ggQihao + "_" + ggType + "_" + ggPage + ".jpg";
			String url = "/" + ggQihao + "/" + fileName;
			gg.setGgFileUrl(url);

			String imagePath = imageDir + "/" + fileName;
			File file = new File(imagePath);
			// 如果文件不存在，对pdf进行截图，生成文件
			if (!file.exists()) {
				imageList.add(imagePath);
				Integer pageInt = new Integer(ggPageNo);
				
				pageNoList.add(pageInt);
				splitPdf = true;

			}
		}

		if (splitPdf) {
			int len = pageNoList.size();
			int[] pageNumbers = new int[len];
			int j = 0;
			for (Integer ggPage : pageNoList) {
				int value = ggPage.intValue();
				pageNumbers[j] = value;
				j++;
			}

			String[] imagePaths = imageList.toArray(new String[len]);
//			if(!ggFilePath.contains("1702_1801")&&
//					!ggFilePath.contains("8103_8202")&&
//					!ggFilePath.contains("8203_8302")&&
//					!ggFilePath.contains("8603_8702")&&
//					!ggFilePath.contains("8603_8702")&&
//					!ggFilePath.contains("8903_9002")){
				// 对公告文件进行截图，保存到指定目录下
				PDFBoxTool.splitPdfToImage(ggFilePath, imagePaths, pageNumbers);
//			}
			
		}

	}

	public static void processGongGao(String ggFilePath, String imageDir,
			List<GongGao> gonggaoList) throws Exception {
		System.out.println("处理文件："+ggFilePath);
		if (gonggaoList != null && gonggaoList.size() > 0) {
			// 处理公告内容
			List<MonitorGongGao> ggList = getGongGaoData(gonggaoList);

			int size=ggList.size();		
			if (ggList != null && size > 0) {
				// 对公告文件进行截图，保存到指定目录下。
				getGgPage(ggFilePath, imageDir, ggList);
				Iterator<MonitorGongGao> it = ggList.iterator();
				while (it.hasNext()) {
					MonitorGongGao gg=it.next();
					String tmType=gg.getTmType();
					if (tmType==null || tmType.equals("")){
						int pageNumber=gg.getLocationPage();
						String regNumber=gg.getRegNumber();
						System.out.println("tmType is null in " +pageNumber+" page" + " regNumber: " +regNumber);
						it.remove();
					}
				}
				// 将公告数据插入数据库
				insertMonitorGongGaoTable(ggList);
			}
		}
	}

	// 向客户的代理人发送邮件，提醒代理人有新的送达公告
	public static void sendTiXingEmail(Map<Integer, List<MonitorGongGao>> custGg)
			throws Exception {

		Iterator<Entry<Integer, List<MonitorGongGao>>> iter = custGg.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, List<MonitorGongGao>> entry = (Map.Entry<Integer, List<MonitorGongGao>>) iter
					.next();
			Integer hgjCustId = (Integer) entry.getKey();
			List<MonitorGongGao> ggList = (List<MonitorGongGao>) entry
					.getValue();
			List<String> urlList = new ArrayList<String>();
			int ggQihao = 0;
			String ggDate = null;
			for (MonitorGongGao gg : ggList) {
				if (ggQihao == 0) {
					ggQihao = gg.getGgQihao();
				}
				if (ggDate == null) {
					Date date = gg.getGgDate();
					if (date != null) {
						ggDate = DateTool.getDate(date);
					}
				}
				String url = gg.getGgFileUrl();
				urlList.add(url);
			}

			// 获取客户的代理人，代理人组长，部门经理
			List<Users> userList = getUserList(hgjCustId);

			// 邮件接收人列表
			List<String> mailToList = new ArrayList<String>();
			for (Users user : userList) {
				String email = user.getEmail();
				if (email != null && !email.equals("")) {
					mailToList.add(email);
				}
			}

			// 公告截图文件所在的目录
			List<String> attachFileList = new ArrayList<String>();
			String imagePath = Constants.gonggao_pages;
			for (String url : urlList) {
				String filePath = imagePath + url;
				attachFileList.add(filePath);
			}

			// 邮件主题
			String subject = ggQihao + "送达公告";

			if (ggDate != null) {
				subject = subject + "(" + ggDate + ")";
			}

			// 邮件正文中的访问公告详情页的链接
			String httpLink = Constants.ggHttpLink;

			// 向客户的代理人发送邮件，提醒代理人有新的送达公告
			MailSender.sendMail(subject, httpLink, mailToList, attachFileList);

		}

	}

	public static String getGgQihao(String pdfFile) {
		String ggQihao = null;
		String fileSeparator = "/";
		if (pdfFile.indexOf("\\") > -1) {
			fileSeparator = "\\";
		}
		String fileName = null;
		int pos = pdfFile.lastIndexOf(fileSeparator);
		if (pos > -1) {
			fileName = pdfFile.substring(pos + 1);
		}

		// 根据文件名称前缀就可以确定公告期号
		int qhPos = fileName.indexOf("_");
		if (qhPos > -1) {
			ggQihao = fileName.substring(0, qhPos);
		}

		return ggQihao;

	}

}
