package com.tmkoo.searchapi.vo;

public class TradeMarkCategory {
    private Integer id;

    private String name;

    private String enname;

    private Integer no;

    private String tmgroup;

    private Integer tmtype;
    
    private String regnumber;
    
    private Integer tmId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getEnname() {
        return enname;
    }

    public void setEnname(String enname) {
        this.enname = enname == null ? null : enname.trim();
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getTmgroup() {
        return tmgroup;
    }

    public void setTmgroup(String tmgroup) {
        this.tmgroup = tmgroup == null ? null : tmgroup.trim();
    }

    public Integer getTmtype() {
        return tmtype;
    }

    public void setTmtype(Integer tmtype) {
        this.tmtype = tmtype;
    }

	public String getRegnumber() {
		return regnumber;
	}

	public void setRegnumber(String regnumber) {
		this.regnumber = regnumber;
	}

	public Integer getTmId() {
		return tmId;
	}

	public void setTmId(Integer tmId) {
		this.tmId = tmId;
	}
	
}