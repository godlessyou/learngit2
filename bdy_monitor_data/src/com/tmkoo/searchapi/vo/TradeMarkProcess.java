package com.tmkoo.searchapi.vo;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonFormat;

public class TradeMarkProcess {
    private Integer id;

    private String status;
   
    private Date statusdate;

    private Integer tmid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
    
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public Date getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(Date statusdate) {
        this.statusdate = statusdate;
    }

    public Integer getTmid() {
        return tmid;
    }

    public void setTmid(Integer tmid) {
        this.tmid = tmid;
    }
}