package com.tmkoo.searchapi.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.tmkoo.searchapi.common.Constants;
import com.tmkoo.searchapi.vo.GongGao;

/**
 * <p>
 * Title: pdf extraction
 * </p>
 * <p>
 * Description: email:chris@matrix.org.cn
 * </p>
 * <p>
 * Copyright: Matrix Copyright (c) 2003
 * </p>
 * <p>
 * Company: Matrix.org.cn
 * </p>
 * 
 * @author chris
 * @version 1.0,who use this example pls remain the declare
 */

public class PdfToText {
	
	private static final String xpdfPath=Constants.xpdfPath;
	
	public List<GongGao>  readPdfFile(String pdfPath, boolean findGgDate, Date ggDate, String ggQihao) throws Exception {
				
		String content=readPdfContent(pdfPath);
		
		List<GongGao> contentList=processContent(content, pdfPath, findGgDate, ggDate, ggQihao);
		
		return contentList;
	
	}
	
	
	public String readPdfContent(String filePath) throws Exception {
		
		String[] cmd = new String[] { xpdfPath, "-raw", "-enc", "UTF-8", "-q",
				filePath, "-" };
		Process p = Runtime.getRuntime().exec(cmd);
		BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
		InputStreamReader reader = new InputStreamReader(bis, "UTF-8");
		StringWriter out = new StringWriter();
		char[] buf = new char[10000];
		int len;
		String content=null;
		while ((len = reader.read(buf)) >= 0) {
			
			String strRead  = new String(buf, 0, len).toUpperCase();  
			// out.write(buf, 0, len);
//			System.out.println("strRead: " + strRead);
			
			if (strRead == null || strRead.equals("")) {
				continue;
			}
			if (content==null){
				content=strRead;
			}else{
				content=content+strRead;
			}
			
		}
		reader.close();
		
		return content;		
	
	}
	
	
	
	private  List<GongGao> processContent(String content, String filePath, boolean findGgDate, Date ggDate, String ggQihao) throws Exception {
			
		List<GongGao> contentList = new ArrayList<GongGao>();
		
		String ggType = "商标初步审定公告";
		
		boolean findTitle = false;		
		
		int pageNo=0;
		
		List<String> ggContents = new ArrayList<String>();
		
		StringTokenizer tok = new StringTokenizer(content, "\n\r");			
		while (tok.hasMoreTokens()) {
			
//			if (pageNo==84){
//				System.out.println("11111111111");
//			}
					
			String line = tok.nextToken();
			line = line.trim();
			if (line == null || line.equals("")) {
				continue;
			}

			if (findGgDate) {
				if (!findTitle) {
					ggQihao=processGgQihao(line);
					if (ggQihao!=null) {						
						findTitle = true;
						continue;
					}
				} else {					
					String ggDateString = processGgDate(line);
					if (ggDateString!=null) {					
						ggDate = DateTool.StringToDate(ggDateString);
						findGgDate = false;
					}
				}
			}	
			
			String start="第";
			String end="号";
			String title="regNumber:";
			String notInclude="类";
			int minSize=5;
			int maxSize=10;
			boolean find=false;
			if (line.startsWith(start) && line.endsWith(end)){
				// 商标注册号不会超过8个字符，不会少于6个字符				
				find=getContent(line, notInclude, title, ggContents, start, end, minSize, maxSize);
				if (find){
					continue;
				}
			}
			
			start="第";
			end="类";
			title="tmType:";
			notInclude=null;
			// 商标国际分类号不会超过3个字符，不会少于1个字符
			minSize=0;
			maxSize=3;
			find=getContent(line, notInclude, title, ggContents, start, end, minSize, maxSize);
			if (find){
				continue;
			}
						
			String pageNumber=processPageNumber(line);			
			if(pageNumber!=null && !pageNumber.equals("")){
				pageNo++;	
					
				addContent(ggContents, contentList, pageNumber, filePath, ggQihao,  ggType,  ggDate,  pageNo);
			}			
			
		}
		
		return contentList;		
	}
	
	
	
	
	
	private void addContent(List<String> ggContents, List<GongGao> contentList, String pageNumber, String filePath, String ggQihao, String ggType, Date ggDate,  int pageNo){
		
		if (pageNo==1){
			//当前读取pdf的采用的是-raw模式，在这个模式下
			//每读取一页，首先读取到页号，然后才读取到每页内容
			//所以，当读取到页号为1时，此时还没有读取到页面内容。
			//只有当读取到下一个页号时，前一页的内容才算读取完成。
			return;
		}	
		
		//在当前文档中的页码
		int currentPage=pageNo-1;
		
		//在本期公告中的页码
		int ggPage = Integer.parseInt(pageNumber);		
		ggPage=ggPage-1;	
				
		if (ggContents == null || ggContents.size()== 0) {
			return;
		}		
		
		List<String> ggList = new ArrayList<String>();
		for(String ct:ggContents){			
			ggList.add(ct);
		}
		ggContents.clear();		
		
		GongGao gongGao = new GongGao();
		gongGao.setContents(ggList);
		
		if (ggPage > 0) {
			gongGao.setGgPage(ggPage);
		}
		gongGao.setGgFilePath(filePath);
		gongGao.setGgType(ggType);				
		gongGao.setGgPageNo(currentPage);		
		if (ggQihao != null) {
			Integer qihao = new Integer(ggQihao);
			int hao = qihao.intValue();
			gongGao.setGgQihao(hao);
		}		
		if (ggDate != null) {
			gongGao.setGgDate(ggDate);
		}
		
		contentList.add(gongGao);			
		
	}
	
	
	
	private boolean getContent(String line, String notInclude, String title, List<String> ggContents, String start, String end, int minSize, int maxSize){
		boolean find=false;		
		if(notInclude!=null){
			int pos=line.indexOf(notInclude);
			if(pos>-1){
				return find;
			}
		}
			
		int len=line.length();		
		int pos1=line.indexOf(start);		
		int pos2=line.indexOf(end);	
		
		
		if (pos1 > -1 && pos2 > -1 && pos1<pos2 && pos2<len) {	
			
			line=line.substring(pos1+1, pos2);
			line=line.trim();
			int len2=line.length();
			if (len2 < maxSize && len2 > minSize){
				String result=title+line;
				ggContents.add(result);
				find=true;
			}else{
				System.out.println(line);
			}
		}		
		return find;
		
	}
	

	private String processGgQihao(String line){
		int pos1=line.indexOf("第");
		int pos2= line.indexOf("期");
		if (pos1> -1 && pos2 > -1) {
			String tempQihao=line.substring(pos1+1, pos2);
			String ggQihao=tempQihao.trim();
			
			return ggQihao;
		}
		return null;
		
	}
	
	
	private String processGgDate(String line){
		if (line.indexOf("年") > -1 && line.indexOf("月") > -1
				&& line.indexOf("日") > -1) {
			int posYear = line.indexOf("年");
			int posMonth = line.indexOf("月");
			int posDay = line.lastIndexOf("日");

			String year = line.substring(posYear-5, posYear);
			year = year.trim();
			String month = line.substring(posYear + 1,
					posMonth);
			month = month.trim();
			String day = line.substring(posMonth + 1, posDay);
			day = day.trim();
			String ggDateString = year + "-" + month + "-"
					+ day;
			return ggDateString;
		}
		return null;
		
	}
	
	
	private String processPageNumber(String s){
		
		if(!s.startsWith(".") || !s.endsWith(".") ){ //不以.开头和结尾的都不是页码
			return null;
		}
		
		int pointLocation=s.indexOf(".");
		if (pointLocation > -1) {
			int len=s.length();
			if (pointLocation<len){
				String partStr=s.substring(pointLocation+1);
				int point2=partStr.indexOf(".");
				if (point2>-1){
					String leftStr=partStr.substring(0,point2);
					String pageNumber=leftStr.trim();					
					// 正确的页码不可能大于10位数
					int length=pageNumber.length();
					if (length>10){
						return null;
					}
					boolean isNumber=StringUtils.isNumeric(pageNumber);					
					if (isNumber){
						return pageNumber;
					}
				}					
			}
		}
		
		return null;
	}
	
	
	public static void main(String args[]) throws Exception {
		
		String filePath="C:\\support\\gonggao\\1559\\1559_商标初步审定公告_8103_8202.pdf";
					
		PdfToText pdfToText=new PdfToText();		
		
		pdfToText.readPdfFile(filePath, true, null, null);
	}
}