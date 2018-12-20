package com.tmkoo.searchapi.vo;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonFormat;


public class TradeMark {
    private Integer id;

    private String appnumber;
  
	private String regnumber;

    private String tmtype;

    private String tmgroup;
    
    private String imgFileName;

    private String tmimage;

    private String tmname;

    private String applicantname;

    private String applicantaddress;

    private String applicantenname;

    private String applicantenaddress;

    private String gtapplicantname;

    private String gtapplicantaddress;

    private String status;
    
    private Date appdate;  
    
    private String approvalnumber;  
    
    private Date approvaldate;
    
    private String regnoticenumber; 
    
    private Date regnoticedate;  
    
    private Date validstartdate; 
    
    private Date validenddate; 
    
    private String tmcategory; 
    
	private String agent;  
	
    private String classify;
    
    private String country; 
    
    private String tujing; 
    //代理文号
    private String caseno;
    
    private Date modifyDate;
    
    public String getAppnumber() {
  		return appnumber;
  	}

  	public void setAppnumber(String appnumber) {
  		this.appnumber = appnumber;
  	}
    
    public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTujing() {
		return tujing;
	}

	public void setTujing(String tujing) {
		this.tujing = tujing;
	}


   

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegnumber() {
        return regnumber;
    }

    public void setRegnumber(String regnumber) {
        this.regnumber = regnumber == null ? null : regnumber.trim();
    }

    public String getTmtype() {
        return tmtype;
    }

    public void setTmtype(String tmtype) {
        this.tmtype = tmtype == null ? null : tmtype.trim();
    }

    public String getTmgroup() {
        return tmgroup;
    }

    public void setTmgroup(String tmgroup) {
        this.tmgroup = tmgroup == null ? null : tmgroup.trim();
    }

    public String getTmimage() {
        return tmimage;
    }

    public void setTmimage(String tmimage) {
        this.tmimage = tmimage == null ? null : tmimage.trim();
    }

    public String getTmname() {
        return tmname;
    }

    public void setTmname(String tmname) {
        this.tmname = tmname == null ? null : tmname.trim();
    }

    public String getApplicantname() {
        return applicantname;
    }

    public void setApplicantname(String applicantname) {
        this.applicantname = applicantname == null ? null : applicantname.trim();
    }

    public String getApplicantaddress() {
        return applicantaddress;
    }

    public void setApplicantaddress(String applicantaddress) {
        this.applicantaddress = applicantaddress == null ? null : applicantaddress.trim();
    }

    public String getApplicantenname() {
        return applicantenname;
    }

    public void setApplicantenname(String applicantenname) {
        this.applicantenname = applicantenname == null ? null : applicantenname.trim();
    }

    public String getApplicantenaddress() {
        return applicantenaddress;
    }

    public void setApplicantenaddress(String applicantenaddress) {
        this.applicantenaddress = applicantenaddress == null ? null : applicantenaddress.trim();
    }

    public String getGtapplicantname() {
        return gtapplicantname;
    }

    public void setGtapplicantname(String gtapplicantname) {
        this.gtapplicantname = gtapplicantname == null ? null : gtapplicantname.trim();
    }

    public String getGtapplicantaddress() {
        return gtapplicantaddress;
    }

    public void setGtapplicantaddress(String gtapplicantaddress) {
        this.gtapplicantaddress = gtapplicantaddress == null ? null : gtapplicantaddress.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
    
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public Date getAppdate() {
        return appdate;
    }

    public void setAppdate(Date appdate) {
        this.appdate = appdate;
    }

    public String getApprovalnumber() {
        return approvalnumber;
    }

    public void setApprovalnumber(String approvalnumber) {
        this.approvalnumber = approvalnumber == null ? null : approvalnumber.trim();
    }
    
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public Date getApprovaldate() {
        return approvaldate;
    }

    public void setApprovaldate(Date approvaldate) {
        this.approvaldate = approvaldate;
    }

    public String getRegnoticenumber() {
        return regnoticenumber;
    }

    public void setRegnoticenumber(String regnoticenumber) {
        this.regnoticenumber = regnoticenumber == null ? null : regnoticenumber.trim();
    }
    
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public Date getRegnoticedate() {
        return regnoticedate;
    }

    public void setRegnoticedate(Date regnoticedate) {
        this.regnoticedate = regnoticedate;
    }
    
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public Date getValidstartdate() {
        return validstartdate;
    }

    public void setValidstartdate(Date validstartdate) {
        this.validstartdate = validstartdate;
    }
    
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public Date getValidenddate() {
        return validenddate;
    }

    public void setValidenddate(Date validenddate) {
        this.validenddate = validenddate;
    }

	


	public String getTmcategory() {
		return tmcategory;
	}

	public void setTmcategory(String tmcategory) {
		this.tmcategory = tmcategory;
	}
	
	 
    public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public String getCaseno() {
		return caseno;
	}

	public void setCaseno(String caseno) {
		this.caseno = caseno;
	}

	public String getImgFileName() {
		return imgFileName;
	}

	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	
}