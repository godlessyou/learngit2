package com.tmkoo.searchapi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
/**
 * 数据思路：
 * 复制D:\software\solr-6.6.0\server\solr\core_1558\conf文件夹，改名为core_期号
 * 修改data-config.xml，或创建该文件覆盖原有文件
 * 发送http请求，先create core 再full-import
 * 图片核心思路：
 * 复制D:\software\solr-6.6.0\server\solr\bdymonitorimage_1558\conf文件夹，改名为bdymonitorimage_期号
 * 修改data-config.xml，或创建该文件覆盖原有文件
 * 发送http请求，先create core 再full-import
 * @author User
 *
 */
public class CreateSolrCore {
	
	//增加数据的核心
	public void addDataCore(int qiHao,String basicPath) throws Exception{
		String sourceFile = basicPath+"/core_basic/conf";
		String targetFile = basicPath+"/core_"+qiHao+"/conf";
		//如果没有core_1558/conf文件夹则进行创建
		if(!new File(targetFile).exists()){
			FileUtil.createFolderIfNotExists(targetFile);
			FileUtil.copyDir(sourceFile, targetFile);
		}
		
		//修改文件data-config
		String dataConfigFile = targetFile+"/data-config.xml";
		updateDataConfigXml(dataConfigFile, qiHao);
		
		//创建核心
		String createCoreUrl = "http://localhost:8984/solr/admin/cores?action=CREATE"
				+"&name=core_"+qiHao
				+"&instanceDir="+basicPath+"/core_"+qiHao
				+"&config=solrconfig.xml"
				+"&schema=schema.xml"
				+"&dataDir=data";
		String returnMsg1 = requestUrl(createCoreUrl);
		String msg=parseMsg(returnMsg1);//解析错误信息
		if(msg!=null){
			System.out.println(msg);
		}

		//创建索引full-import
		String importUrl="http://localhost:8984/solr/core_"+qiHao+"/dataimport?command=full-import";
		String returnMsg2 = requestUrl(importUrl);
		msg = parseMsg(returnMsg2);
		if(msg!=null){
			System.out.println(msg);
		}
	}
	//增加图片的核心
	public void addImageCore(String basicPath,int num) throws Exception{
		String sourceFile = basicPath+"/bdymonitorimage_base/conf";
		String targetFile = basicPath+"/bdymonitorimage_"+num+"/conf";
		//如果没有bdymonitorimage_1/conf文件夹则进行创建
		if(new File(targetFile).exists()){//已存在该核心，则增量添加索引
			while(true){
				String importUrl="http://localhost:8984/solr/bdymonitorimage_"+num+"/dataimport?command=full-import";
				String returnMsg1 = requestUrl(importUrl);
				Thread.sleep(5000);
				if(returnMsg1.indexOf("completed")>0){
					System.out.println("图片索引更新成功");
					break;
				}
			}
			return ;
		}
		FileUtil.createFolderIfNotExists(targetFile);
		FileUtil.copyDir(sourceFile, targetFile);
		//修改文件data-config
		String dataConfigFile = targetFile+"/solr-data-config.xml";
		updateImageDataConfigXml(dataConfigFile, num);

		//创建核心
		String createCoreUrl = "http://localhost:8984/solr/admin/cores?action=CREATE"
				+"&name=bdymonitorimage_"+num
				+"&instanceDir="+basicPath+"/bdymonitorimage_"+num
				+"&config=solrconfig.xml"
				+"&schema=schema.xml"
				+"&dataDir=data";
		String returnMsg1 = requestUrl(createCoreUrl);
		String msg=parseMsg(returnMsg1);//解析错误信息
		if(msg!=null){
			System.out.println(msg);
		}

		//创建索引full-import
		while(true){
			String importUrl="http://localhost:8984/solr/bdymonitorimage_"+num+"/dataimport?command=full-import";
			String returnMsg2 = requestUrl(importUrl);
			Thread.sleep(5000);
			if(returnMsg2.indexOf("completed")>0){
				System.out.println("bdymonitorimage_"+num+"核心创建成功，添加索引成功");
				break;
			}
		}
	}
	public void updateDataConfigXml(String filePath,int qiHao) throws Exception{
		//创建Document对象，读取已存在的Xml文件
        Document doc=new SAXReader().read(new File(filePath));
        Element dataConfig = doc.getRootElement();
        Element dataSource=dataConfig.element("dataSource");
        dataSource.addAttribute("url", "jdbc:mysql://192.168.0.8:3306/bdy_db");
        dataSource.addAttribute("user", "bdyuser");
        dataSource.addAttribute("password", "123456");
        Element entity = dataConfig.element("document").element("entity");
        entity.addAttribute("query", "select * from monitorgonggao where ggQihao = "+qiHao);
        FileOutputStream out = new FileOutputStream(filePath);
        OutputFormat format = OutputFormat.createCompactFormat();
        format.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(out,format);
        writer.write(doc);
        writer.close();
	}
	/**
	 * 更新图片的dataconfigxml文件
	 * @param filePath
	 * @param num 第几个存放图片的文件夹
	 * @throws Exception
	 */
	public void updateImageDataConfigXml(String filePath,int num) throws Exception{
		//创建Document对象，读取已存在的Xml文件
        Document doc=new SAXReader().read(new File(filePath));
        Element dataConfig = doc.getRootElement();
        Element document=dataConfig.element("document");
        Element entity = document.element("entity");
        entity.addAttribute("baseDir", "C:/tmimage/monitortm/downloadimage/"+num+"/");
        FileOutputStream out = new FileOutputStream(filePath);
        OutputFormat format = OutputFormat.createCompactFormat();
        format.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(out,format);
        writer.write(doc);
        writer.close();
	}
	public String requestUrl(String url) throws Exception{
		return GraspUtil.getText(url);
	}
	//解析xml的错误信息
	public String parseMsg(String msg) throws DocumentException{
		String error=null;
		Document document = DocumentHelper.parseText(msg);
        // 获取根结点对象
        Element rootElement = document.getRootElement();
        Node msgNode = rootElement.selectSingleNode("//str[@name='msg']");
        if(msgNode!=null){
        	error =msgNode.getText();
        }
		return error;
	}
	//增量添加索引delta-import
	public void deltaImport() throws Exception{
		while(true){
			String importUrl="http://localhost:8984/solr/bdymonitor/dataimport?command=delta-import";
			String returnMsg2 = requestUrl(importUrl);
			Thread.sleep(5000); //每隔4秒刷新，如果返回值出现Indexing completed. Added/Updated: 11999 documents. Deleted 0 documents.则完成索引增加
			if(returnMsg2.indexOf("completed")>0){
				System.out.println("公告数据索引增量更新成功");
				break;
			}
		}
	}
	//全量添加索引
	public void fullImport() throws Exception{
		while(true){
			String importUrl="http://localhost:8984/solr/bdymonitor/dataimport?command=full-import";
			String returnMsg2 = requestUrl(importUrl);
			Thread.sleep(5000); //每隔4秒刷新，如果返回值出现Indexing completed. Added/Updated: 11999 documents. Deleted 0 documents.则完成索引增加
			if(returnMsg2.indexOf("completed")>0){
				System.out.println("公告数据索引增加成功");
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		int qiHao = 1558;
		String basicPath="D:/solr6/server/solr";
		int num = 1;
		try {
			new CreateSolrCore().addImageCore(basicPath, num);
//			new CreateSolrCore().deltaImport();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
