package com.tmkoo.searchapi.vo;

import java.util.Date;

public class MonitorGongGao {
    private Integer id;

    private String regNumber;

    private String tmNameCn;

    private String tmNameEn;

    private String tmPinyin;

    private String tmImage;
    
    private String imagePath;

    private String imageLink;

    private String tmType;

    private String tmGroup;

    private String appName;

    private String agent;
    
    private Integer monitorType;

    private Date ggDate;

    private Integer ggQihao;

    private Integer ggPage;

    private Integer locationPage;

    private String ggFilePath;   

    private String ggFileUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber == null ? null : regNumber.trim();
    }

    public String getTmNameCn() {
        return tmNameCn;
    }

    public void setTmNameCn(String tmNameCn) {
        this.tmNameCn = tmNameCn == null ? null : tmNameCn.trim();
    }

    public String getTmNameEn() {
        return tmNameEn;
    }

    public void setTmNameEn(String tmNameEn) {
        this.tmNameEn = tmNameEn == null ? null : tmNameEn.trim();
    }

    public String getTmPinyin() {
        return tmPinyin;
    }

    public void setTmPinyin(String tmPinyin) {
        this.tmPinyin = tmPinyin == null ? null : tmPinyin.trim();
    }

    public String getTmImage() {
        return tmImage;
    }

    public void setTmImage(String tmImage) {
        this.tmImage = tmImage == null ? null : tmImage.trim();
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink == null ? null : imageLink.trim();
    }

    public String getTmType() {
        return tmType;
    }

    public void setTmType(String tmType) {
        this.tmType = tmType == null ? null : tmType.trim();
    }

    public String getTmGroup() {
        return tmGroup;
    }

    public void setTmGroup(String tmGroup) {
        this.tmGroup = tmGroup == null ? null : tmGroup.trim();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent == null ? null : agent.trim();
    }


    public Date getGgDate() {
        return ggDate;
    }

    public void setGgDate(Date ggDate) {
        this.ggDate = ggDate;
    }

    public Integer getGgQihao() {
        return ggQihao;
    }

    public void setGgQihao(Integer ggQihao) {
        this.ggQihao = ggQihao;
    }

    public Integer getGgPage() {
        return ggPage;
    }

    public void setGgPage(Integer ggPage) {
        this.ggPage = ggPage;
    }

    public Integer getLocationPage() {
        return locationPage;
    }

    public void setLocationPage(Integer locationPage) {
        this.locationPage = locationPage;
    }

    public String getGgFilePath() {
        return ggFilePath;
    }

    public void setGgFilePath(String ggFilePath) {
        this.ggFilePath = ggFilePath == null ? null : ggFilePath.trim();
    }

    public String getGgFileUrl() {
        return ggFileUrl;
    }

    public void setGgFileUrl(String ggFileUrl) {
        this.ggFileUrl = ggFileUrl == null ? null : ggFileUrl.trim();
    }

	public Integer getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(Integer monitorType) {
		this.monitorType = monitorType;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
}