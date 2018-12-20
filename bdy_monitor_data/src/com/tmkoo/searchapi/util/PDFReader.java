package com.tmkoo.searchapi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.tmkoo.searchapi.common.Constants;
import com.tmkoo.searchapi.vo.GongGao;

public class PDFReader {

	private static Logger logger = Logger.getLogger(PDFReader.class);

	public static void readPDF(String fileName) {
		File file = new File(fileName);
		RandomAccessFile in = null;
		try {
			// in = new FileInputStream(fileName);
			in = new RandomAccessFile(file, "r");

			// 新建一个PDF解析器对象
			PDFParser parser = new PDFParser(in);
			// 对PDF文件进行解析
			parser.parse();
			// 获取解析后得到的PDF文档对象
			PDDocument pdfdocument = parser.getPDDocument();
			// 新建一个PDF文本剥离器
			PDFTextStripper stripper = new PDFTextStripper();
			// 从PDF文档对象中剥离文本
			String result = stripper.getText(pdfdocument);
			System.out.println("PDF文件的文本内容如下：");
			System.out.println(result);

		} catch (Exception e) {
			System.out.println("读取PDF文件" + file.getAbsolutePath() + "生失败！" + e);
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读PDF文件，使用了pdfbox开源项目
	 * 
	 * @param filePath
	 */
	public static List<GongGao> readPdfFile(String filePath,
			boolean findGgDate, Date ggDate, String ggQihao) throws Exception {

		List<GongGao> contentList = new ArrayList<GongGao>();
		File pdfFile = new File(filePath);

		String ggType = "商标初步审定公告";

	

		FileInputStream is = new FileInputStream(pdfFile);

		PDDocument document = PDDocument.load(is);

		try {

			// 获取页码
			int pages = document.getNumberOfPages();
			// 读文本内容
			PDFTextStripper stripper = new PDFTextStripper();
			// 设置按顺序输出
			stripper.setSortByPosition(true);

			for (int i = 1; i < pages; i++) {
				stripper.setStartPage(i);
				stripper.setEndPage(i);
				String content = stripper.getText(document);
				// System.out.println(content);
				if (content == null || content.equals("")) {
					continue;
				}

				// boolean hasGgQihao=false;
				List<String> ggContents = new ArrayList<String>();

				StringTokenizer tok = new StringTokenizer(content, "\n\r");
				boolean find = false;
				boolean findTitle = false;
				String lastLine = null;
				while (tok.hasMoreTokens()) {
					String s = tok.nextToken();
					s = s.trim();
					if (s == null || s.equals("")) {
						continue;
					}
					lastLine = s;

					if (findGgDate) {
						if (!findTitle) {
							if (s.indexOf("商标初步审定公告") > -1
									&& s.indexOf("第") > -1) {
								findTitle = true;
								continue;
							}
						} else {
							if (s.indexOf("年") > -1 && s.indexOf("月") > -1
									&& s.indexOf("日") > -1) {
								int posYear = s.indexOf("年");
								int posMonth = s.indexOf("月");
								int posDay = s.indexOf("日");

								String year = s.substring(0, posYear);
								year = year.trim();
								String month = s.substring(posYear + 1,
										posMonth);
								month = month.trim();
								String day = s.substring(posMonth + 1, posDay);
								day = day.trim();
								String ggDateString = year + "-" + month + "-"
										+ day;
								ggDate = DateTool.StringToDate(ggDateString);
								findGgDate = false;
							}
						}
					}
					
					if (ggType != null && ggType.equals("商标初步审定公告")) {
						if (s.indexOf("第") > -1 && s.indexOf("号") > -1) {
							ggContents.add(s);
						}
						continue;
					}

					if (ggType != null && ggType.equals("送达公告")) {
						// 如果还没有找到需要的公告名称，先查找符合条件的公告名称所在的那一行
						if (!find) {
							for (String name : Constants.gongGaoNames) {
								if (s.indexOf(name) > -1) {
									find = true;
									break;
								}
							}
							if (find) {
								ggContents.add(s);
								continue;
							}
						}

						if (find) {
							// 每一行公告内容包括：注册号/申请号 商标 类别 原发文日期 原发文编号 收件人名称
							// 因此，每一行公告内容至少有5个空格，除了编号内容在一行写不下，换行后，形成多行的。
							StringTokenizer tok2 = new StringTokenizer(s, " ");
							int countTokens = tok2.countTokens();
							if (countTokens < 5) {
								int haoPos = s.indexOf("号");
								if (haoPos < 0) {
									// 如果公告内容少于5列，意味着上一个公告名称下的内容已经读完了，当前一行是一个新的公告名称
									find = false;
									continue;
								}
							}
							ggContents.add(s);
						}

					}

				}

				if (ggContents != null && ggContents.size() > 0) {
					int ggPage = 0;
					if (lastLine != null) {
						
						String pageNumber = StringUtils.getNumber(lastLine);
						ggPage = Integer.parseInt(pageNumber);
					}

					GongGao gongGao = new GongGao();
					if (ggPage > 0) {
						gongGao.setGgPage(ggPage);
					}
					gongGao.setGgFilePath(filePath);
					gongGao.setGgType(ggType);
					gongGao.setGgPageNo(i);
					if (ggQihao != null) {
						Integer qihao = new Integer(ggQihao);
						int hao = qihao.intValue();
						gongGao.setGgQihao(hao);
					}
					gongGao.setContents(ggContents);
					if (ggDate != null) {
						gongGao.setGgDate(ggDate);
					}
					contentList.add(gongGao);

				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (is != null)
				is.close();
			if (document != null)
				document.close();
		}

		return contentList;

	}

	/**
	 * 读PDF文件，使用了pdfbox开源项目
	 * 
	 * @param filePath
	 */
	public static List<String> getPdfFile(String gonggaoPath) throws Exception {
		List<String> filePathList = new ArrayList<String>();

		if (gonggaoPath.endsWith("pdf")) {
			filePathList.add(gonggaoPath);
		} else {

			File srcFile = new File(gonggaoPath);
			String filePath = "";
			if (srcFile.isDirectory()) {
				filePath = gonggaoPath;
			}
			if (gonggaoPath.endsWith(".zip")) {

				int beginIndex = gonggaoPath.lastIndexOf(".");

				// 获取公告目录
				filePath = gonggaoPath.substring(0, beginIndex);

				File dir = new File(filePath);

				if (!dir.exists()) {
					// 将rar或者zip文件进行解压
					ZipUtils.decompress(gonggaoPath);
				}
				if (!dir.exists()) {
					logger.info("解压文件" + gonggaoPath + "失败！");
					return null;
				}
			}

			File dir = new File(filePath);
			if (!dir.exists()) {
				logger.info(filePath + "目录下不存在！");
				return null;
			}

			String[] extNames = { "pdf" };
			String specailfileName = "送达公告";
			FileUtil.getSpecalFile(filePath, extNames, filePathList,
					specailfileName);

			specailfileName = "_商标初步审定公告";
			FileUtil.getSpecalFile(filePath, extNames, filePathList,
					specailfileName);
		}

		int size = filePathList.size();
		if (size == 0) {
			logger.info("送达公告文件不存在！");
			return null;
		}
		return filePathList;
	}

	public static List<List<GongGao>> getPdfContents(String gonggaoPath)
			throws Exception {

		List<List<GongGao>> contentList = new ArrayList<List<GongGao>>();

		Date ggDate = null;

		String ggQihao = null;

		List<String> filePathList = getPdfFile(gonggaoPath);
		if (filePathList != null) {

			for (String pdfFile : filePathList) {

				if (ggQihao == null) {
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

				}

				String filekeyName = "_商标初步审定公告_1_";
				if (pdfFile.indexOf(filekeyName) > -1) {
					List<GongGao> list = readPdfFile(pdfFile, true, null,
							ggQihao);
					contentList.add(list);
					// 获取公告日期
					if (list != null && list.size() > 0) {
						for (GongGao gg : list) {
							ggDate = gg.getGgDate();
							if (ggDate != null) {
								break;
							}
						}
					}
					break;
				}
			}

			for (String pdfFile : filePathList) {
				String filekeyName = "_商标初步审定公告_1_";
				if (pdfFile.indexOf(filekeyName) > -1) {
					continue;
				}
				List<GongGao> list = readPdfFile(pdfFile, false, ggDate,
						ggQihao);
				contentList.add(list);
			}
		}

		return contentList;
	}

	

	public static void main(String[] args) {

		// String gonggaoPath = "C:\\work\\gonggao\\1550.rar";
		try {
			// read(pdfFile);
			// List<List<String>> contentList = getPdfContents(gonggaoPath);
			String s = ".１４５４９.";
			StringUtils.getNumber(s);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}