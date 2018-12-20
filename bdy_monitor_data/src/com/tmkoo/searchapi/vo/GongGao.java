package com.tmkoo.searchapi.vo;

import java.util.Date;
import java.util.List;

public class GongGao {
	
	private String ggFilePath;

	private String ggType;
	
	// 在所有当期公告（一般都是多个pdf）中的总的页号，例如：14559
    private int ggPage;
    
    // 在当前pdf中位于第几页，例如：10
    private int ggPageNo;
	
	private int ggQihao;
	
	private Date ggDate;
	
	private List<String> contents;
	
	public String getGgFilePath() {
		return ggFilePath;
	}

	public void setGgFilePath(String ggFilePath) {
		this.ggFilePath = ggFilePath;
	}
	
	public int getGgQihao() {
		return ggQihao;
	}

	public void setGgQihao(int ggQihao) {
		this.ggQihao = ggQihao;
	}

	public int getGgPage() {
		return ggPage;
	}

	public void setGgPage(int ggPage) {
		this.ggPage = ggPage;
	}

	public List<String> getContents() {
		return contents;
	}

	public void setContents(List<String> contents) {
		this.contents = contents;
	}

	public String getGgType() {
		return ggType;
	}

	public void setGgType(String ggType) {
		this.ggType = ggType;
	}

	public Date getGgDate() {
		return ggDate;
	}

	public void setGgDate(Date ggDate) {
		this.ggDate = ggDate;
	}

	public int getGgPageNo() {
		return ggPageNo;
	}

	public void setGgPageNo(int ggPageNo) {
		this.ggPageNo = ggPageNo;
	}

	
}
