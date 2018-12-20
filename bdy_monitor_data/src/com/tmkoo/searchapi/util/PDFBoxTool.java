package com.tmkoo.searchapi.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
//import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.rendering.PDFRenderer;
//import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
//import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


public class PDFBoxTool {

	public static void mergeDocuments(String sourceDir[], String destFile) {
		// 合并工具类
		PDFMergerUtility merger = new PDFMergerUtility();
		merger.setDestinationFileName(destFile);
		try {

			for (String pdf : sourceDir) {
				File file = new File(pdf);
				if (file.exists()) {
					merger.addSource(pdf);
				}
			}
			// 合并文件，设置内存参数
			merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void ExtractorDocuments(String args[]) {
		// 拆分工具类
		try {
			
//			String startPage=args[0];
//			String endPage=args[1];
//			String outputPrefix=args[2];
//			String pdfPath=args[3];
//			
//			int start=Integer.parseInt(startPage);			
//			int end=Integer.parseInt(endPage);
//			
//			Splitter splitter = new Splitter();
//			splitter.setStartPage(start);
//			splitter.setEndPage(end);
//			
//			
//			File file = new File(pdfPath);
//			if (file.exists()){
//				PDDocument document = PDDocument.load(file);
//				splitter.split(document);
//			}
			
			PDFSplit.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/*
	public static void createPDFFromImage(String imagePath, String outputFile)
			throws IOException {
		// the document
		PDDocument doc = null;

		try {
			doc = new PDDocument();

			PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath,
					doc);
			// float w = pdImage.getWidth();
			// float h = pdImage.getHeight();
			int basesize = 595;
			ArrayList<String> imageSizes = FileUtil.imageReSize(imagePath,
					basesize);
			String width = imageSizes.get(0);
			String height = imageSizes.get(1);
			Float widthFloat = new Float(width);
			Float heightFloat = new Float(height);
			float w = widthFloat.floatValue();
			float h = heightFloat.floatValue();

			PDPage blankPage = new PDPage(new PDRectangle(w, h));
			doc.addPage(blankPage);
			// we will add the image to the first page.
			PDPage page = doc.getPage(0);

			PDPageContentStream contentStream = new PDPageContentStream(doc,
					page, AppendMode.OVERWRITE, true);

			// contentStream.drawImage(ximage, 20, 20 );
			// better method inspired by
			// http://stackoverflow.com/a/22318681/535646
			// reduce this value if the image is too large
			float scale = 1f;
			// contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(),
			// pdImage.getHeight());
			contentStream.drawImage(pdImage, 0, 0, w, h);

			contentStream.close();
			doc.save(outputFile);
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}


*/
	public static void pdfToJpg(String pdfPath, String imgPath) {
		PDDocument doc = null;
		try {
			File file = new File(pdfPath);
			if (file.exists()){
				doc = PDDocument.load(file);
				PDFRenderer renderer = new PDFRenderer(doc);
				int pageCount = doc.getNumberOfPages();
				for (int i = 0; i < pageCount; i++) {
					BufferedImage image = renderer.renderImageWithDPI(i, 296);
					// BufferedImage image = renderer.renderImage(i, 2.5f);
					String filePath = imgPath + i+ ".jpg";
					ImageIO.write(image, "jpg", new File(filePath));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				doc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	public static void splitPdfToImage(String pdfPath, String [] imagePaths, int [] pageNumbers) throws IOException {
		
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()){
			PDDocument doc = PDDocument.load(pdfFile);
			PDFRenderer renderer = new PDFRenderer(doc);
			
			int len=imagePaths.length;
			BufferedImage bufferedImage = null;
			String imagePath=null;
			for (int i=0; i<len; i++){
				try{
					imagePath=imagePaths[i];
					File file=new File(imagePath);
					if (file.exists()){
						continue;
					}
					int pageNo=pageNumbers[i];
					if(pageNo>0){
						pageNo=pageNo-1;
					}
					bufferedImage = renderer.renderImageWithDPI(pageNo, 296);
					// BufferedImage image = renderer.renderImage(i, 2.5f);	
					ImageIO.write(bufferedImage, "jpg", new File(imagePath));
				}catch(Exception e){
					e.printStackTrace();
					throw e;
				}
			}
		}
		
	}

	public static void main(String args[]) {
		
//		ExtractorDocuments(args);
		
		String pdfPath ="C:/work/gonggao/1558-1/1558_商标初步审定公告_1_200.pdf";
		String [] imagePaths=new String[1];
		int [] pageNumbers=new int[1];
		imagePaths[0]="C:/gonggao/01.jpg";
		pageNumbers[0]=0;
		try {
			splitPdfToImage(pdfPath, imagePaths, pageNumbers);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String pdfPath ="C:/temp/yingyezhizhao.pdf";
//		String imgPath = "C:/temp/pdf/pdfbox_image";
//		pdfToJpg(pdfPath, imgPath);
		
		
//		// 源文件目录
//		String sourceDir[] = { "C:/mailtest/test-1.pdf",
//				"C:/mailtest/test-2.pdf" };
//		// 指定目标文件名称
//		String destFile = "C:/mailtest/merge.pdf";
//
		
//
//		// 指定目标文件名称
//		// String imgFile =
//		// "C:/mailtest/test/41-15098655-HAMSTRING PULLEY-CERTIFICATE.jpg";
//		// String pdfFile =
//		// "C:/mailtest/test/41-15098655-HAMSTRING PULLEY-CERTIFICATE.pdf";
//
//		String imgFile = "C:/mailtest/test/1-14585408-阿兰德拉伯胶及图-PUBLICATION.jpg";
//		String pdfFile = "C:/mailtest/test/1-14585408-阿兰德拉伯胶及图-PUBLICATION.pdf";
//
//		try {
//			createPDFFromImage(imgFile, pdfFile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// mergeDocuments(sourceDir, destFile);

	}
}
