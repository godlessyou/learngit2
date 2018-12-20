package com.tmkoo.searchapi.vo;

public class MonitorTm {
    private Integer id;

    private Integer custId;

    private Integer appId;

    private String tmName;

    private Integer monitAllTmType;

    private String tmType;

    private Integer monitorType;

    private String tmImage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustId() {
        return custId;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getTmName() {
        return tmName;
    }

    public void setTmName(String tmName) {
        this.tmName = tmName == null ? null : tmName.trim();
    }

    public Integer getMonitAllTmType() {
        return monitAllTmType;
    }

    public void setMonitAllTmType(Integer monitAllTmType) {
        this.monitAllTmType = monitAllTmType;
    }

    public String getTmType() {
        return tmType;
    }

    public void setTmType(String tmType) {
        this.tmType = tmType == null ? null : tmType.trim();
    }

    public Integer getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Integer monitorType) {
        this.monitorType = monitorType;
    }

    public String getTmImage() {
        return tmImage;
    }

    public void setTmImage(String tmImage) {
        this.tmImage = tmImage == null ? null : tmImage.trim();
    }
}