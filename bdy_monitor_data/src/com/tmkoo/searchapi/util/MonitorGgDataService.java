package com.tmkoo.searchapi.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.tmkoo.searchapi.common.Constants;
import com.tmkoo.searchapi.common.ReturnInfoNoTotal;
import com.tmkoo.searchapi.vo.GongGao;
import com.tmkoo.searchapi.vo.MonitorGongGao;


@Component
public class MonitorGgDataService extends MonitorGgProcessService {

	public int qiHao=0;

	private static String ggType="商标初步审定公告";

	private  String gongGaoPath=Constants.gonggao_dir;

	private ServiceUrlConfig serviceUrlConfig= new ServiceUrlConfig();

	public int getQiHao() {
		return qiHao;
	}

	public void setQiHao(int qiHao) {
		this.qiHao = qiHao;
	}

	public String getGongGaoPath() {
		return gongGaoPath;
	}

	public void setGongGaoPath(String gongGaoPath) {
		this.gongGaoPath = gongGaoPath;
	}

	public void insertGongGaoData(String ggPath) throws Exception {
		List<String> filePathList = PDFReader.getPdfFile(ggPath);
		String imageDir = null;		
		Date ggDate = null;
		String ggQihao =null;
		boolean createDir=false;	
		PdfToText pdfToText=new PdfToText();		
		if (filePathList != null) {
			for (String pdfFile : filePathList) {				
				String filekeyName = "_商标初步审定公告_1_";
				if (pdfFile.indexOf(filekeyName) > -1) {//存在	第一次		
					List<GongGao> list = pdfToText.readPdfFile(pdfFile, true, null,ggQihao);
					// 获取公告日期
					if (list != null && list.size() > 0) {
						for (GongGao gg : list) {						
							qiHao= gg.getGgQihao();	
							ggQihao=qiHao+"";
							ggDate = gg.getGgDate();
							if (ggDate != null) {
								break;
							}
						}
						if (!createDir){
							if (ggQihao != null && !ggQihao.equals("")) {
								imageDir = Constants.gonggao_pages + "/" + ggQihao;
								// 创建将pdf截图后形成的图片文件所保存的目录						
								FileUtil.createFolderIfNotExists(imageDir);
							}
							createDir=true;
						}
						processGongGao(pdfFile, imageDir, list);
					}
					break;
				}
			}

			for (String pdfFile : filePathList) {
				String filekeyName = "_商标初步审定公告_1_";
				if (pdfFile.indexOf(filekeyName) > -1) {//存在_1的就继续
					continue;
				}
				// 只读取初审公告
				if (pdfFile.indexOf(ggType) <0) { //不存在 商标初步审定公告 的就继续
					continue;
				}
				List<GongGao> list = pdfToText.readPdfFile(pdfFile, false, ggDate, ggQihao);
				processGongGao(pdfFile, imageDir, list);
			}
		}
	}

	public ReturnInfoNoTotal upateGongGao(String ggPath,Integer ggQihao) {
		ReturnInfoNoTotal returnInfo = new ReturnInfoNoTotal();

		try {
			if(ggQihao!=null){
				qiHao=ggQihao;
			}
			logger.info("start insert monitorgonggao table");
			if (qiHao!=0){
				// 解析公告，将数据插入数据库
				try{
					insertGongGaoData(ggPath);
				}catch(Exception e){
					returnInfo.setSuccess(false);
					returnInfo.setMessage("从插入公告数据异常");
					return returnInfo;
				}
			}
			logger.info("end insert monitorgonggao table");	
			if(qiHao>0){

				//从数据库获取公告数据
				List<MonitorGongGao> ggList = getGgList(qiHao);		

				// 利用公告数据中的注册号和商品服务类别
				// 从外部接口获取商标详细信息，并更新数据库。
				GongGaoTmDataService gongGaoTmDataService=new GongGaoTmDataService();
				try{
					gongGaoTmDataService.updateTmDetail(ggList);
				}catch(Exception e){
					returnInfo.setSuccess(false);
					returnInfo.setMessage("从标库更新公告异常");
					return returnInfo;
				}
			}
			//更改数据库ggqihao表
			updateGgqihao(qiHao);
			try{
				new CreateSolrCore().deltaImport();

				//图片核心的创建和导入
				//获取该期公告的id最小值和最大值，确定图片在第几个文件夹，确定num的值
				Map<String, Integer> mm = getMonitorGongGaoId(qiHao);
				//最大值
				int max = mm.get("max");
				//最小值
				int min = mm.get("min");
//				String basicPath = "D:/software/solr-6.6.0/server/solr";
				String basicPath = "D:/solr-6.6.0/server/solr";//0.8
				for(int i=(min/12000)+1;i<=(max/12000)+1;i++){
					new CreateSolrCore().addImageCore(basicPath, i);
				}
			}catch(Exception e){
				e.printStackTrace();
				returnInfo.setSuccess(false);
				returnInfo.setMessage("solr数据导入异常");
				return returnInfo;
			}
			//对新加入的公告进行近似查询
			String url=serviceUrlConfig.getBdymonitorserviceUrl()+"/monitor/monitoralltm?qiHao="+qiHao;
			String jsonString;
			try {
				jsonString = GraspUtil.getText(url);
				returnInfo=JsonUtil.toObject(jsonString, ReturnInfoNoTotal.class);
			} catch (Exception e) {
				e.printStackTrace();
				returnInfo.setSuccess(false);
				returnInfo.setMessage(e.getMessage());
				return returnInfo;
			} 
		}catch (Exception e) {
			e.printStackTrace();
			returnInfo.setSuccess(false);
			returnInfo.setMessage(qiHao+"期公告导入失败");
			return returnInfo;
		}
		returnInfo.setSuccess(true);
		returnInfo.setMessage("导入公告数据成功");
		return returnInfo;
	}

	// 处理命令行参数
	private  void processParameter(String[] args) {

		// 目前参数是商标数据对应的custId，custName（必须输入）
		if (args == null || args.length < 1) {
			logger.info("必须输入公告文件的全路径");
			System.exit(1);
		}
		String param = args[0];
		gongGaoPath=param;

		int len=args.length;
		if (len>1){
			String ggQihao=args[1];
			if (StringUtils.isNumeric(ggQihao)){
				qiHao=Integer.parseInt(ggQihao);
			}
		}
	}

	public static void main(String[] args) {
		try {

			MonitorGgDataService monitorService=new MonitorGgDataService();
			monitorService.upateGongGao("C:/support/gonggao",1558);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
