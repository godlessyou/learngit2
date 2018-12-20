package com.tmkoo.searchapi.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tmkoo.searchapi.vo.AppDataStatus;
import com.tmkoo.searchapi.vo.JsonSbGongGao;
import com.tmkoo.searchapi.vo.JsonTmGongGaoInfo;
import com.tmkoo.searchapi.vo.TmDataStatus;
import com.tmkoo.searchapi.vo.TradeMark;
import com.tmkoo.searchapi.vo.TradeMarkCategory;
import com.tmkoo.searchapi.vo.TradeMarkProcess;

@Component
// Spring Service Bean的标识.
//@Transactional
public class TmDbService extends DataService {

	private static Logger logger = Logger.getLogger(TmDbService.class);

	// 获取custId
	public Integer getCustId(String custName) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		Integer custId = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select custId from  customer where custName=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, custName);

			rs = pstmt.executeQuery();

			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {

					int id = rs.getInt(1);
					custId = new Integer(id);
					break;

				} while (rs.next());
			} else {
				logger.info("数据库内没有符合条件的数据");

			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return custId;
	}

	
	
	// 判断商标图片是否为空
	public boolean tmImageIsNull(String regNumber,String tmType) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select tmId from  trademark where tmType=? and regnumber=? and tmimage is null";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, tmType);
			pstmt.setString(2, regNumber);

			rs = pstmt.executeQuery();

			if (rs == null || !rs.next()) {
				return false;
			}else{
				return true;
			}
			

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		
	}
	
	
	// 获取商标注册号和商标的Id
	public List<Map<String, Integer>> getTradeMarkInfo(Integer custId,
			String appName) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String, Integer>> regNumberList = new ArrayList<Map<String, Integer>>();
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select regnumber, id from  trademark where custid=? and applicantname=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);
			pstmt.setString(2, appName);

			rs = pstmt.executeQuery();

			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {

					Map<String, Integer> map = new HashMap<String, Integer>();
					// 获取regNumber
					String regNumber = rs.getString(1);
					Integer id = rs.getInt(2);
					if (regNumber != null && !regNumber.equals("")) {
						map.put(regNumber, id);
						regNumberList.add(map);
					}

				} while (rs.next());
			} else {
				logger.info("数据库内没有符合条件的数据");

			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return regNumberList;
	}
	
	
	
	
	// 判断潜在客户是否已经更新过一次商标数据
	public boolean isUpdated(Integer custId) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isUpdate=false;
		
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select  a.id from  trademark a, customer b "
					+ "where a.custId=b.custId and b.custType<>10 "
					+ "and a.custid=? and a.modifyDate is not null limit 0,1";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);			

			rs = pstmt.executeQuery();

			if (rs == null || !rs.next()) {
				logger.info("数据库内没有符合条件的数据");
			}
			else{
				isUpdate=true;
			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return isUpdate;
	}
	
	
	// 获取商标注册号和商标的Id
	public List<String> getRegNumber(Integer custId,
			String appName) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<String> regNumberList = new ArrayList<String>();
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select distinct(regnumber) from  trademark where custid=? and applicantname=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);
			pstmt.setString(2, appName);

			rs = pstmt.executeQuery();

			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {

					
					// 获取regNumber
					String regNumber = rs.getString(1);					
					if (regNumber != null && !regNumber.equals("")) {						
						regNumberList.add(regNumber);
					}

				} while (rs.next());
			} else {
				logger.info("数据库内没有符合条件的数据");

			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return regNumberList;
	}
	
	

	// 获取某个申请人的商标数据
	public TradeMark getTradeMark(String regNumber, String tmType,String interval)
			throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TradeMark tm = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select tmId, "
					+ "regnumber, "
					+ "tmtype, "
					+ "approvalnumber, "
					+ "regnoticenumber, "
					+ "validstartdate, "
					+ "validenddate, "
					+ "applicantname, "
					+ "applicantaddress, "
					+ "agent, "
					+ "appdate, "
					+ "tmcategory, "
					+ "imgFileUrl, "
					+ "status "
					+ "from  trademark "
					+ "where regNumber = ? and tmType = ? ";
			if(interval!=null){
				query += "and DATE_ADD(modifyDate, INTERVAL "+interval+" DAY) > curdate()";
			}

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, regNumber);
			pstmt.setString(2, tmType);
			

			rs = pstmt.executeQuery();

			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			
			if (rs.next()) {
				// 获取regNumber
				int id= rs.getInt(1);
				String regnumber = rs.getString(2);
				String tmtype = rs.getString(3);
				String approvalnumber = rs.getString(4);
				String regnoticenumber = rs.getString(5);
				Date validstartdate = rs.getDate(6);
				Date validenddate = rs.getDate(7);
				String applicantname = rs.getString(8);
				String applicantaddress = rs.getString(9);
				String agent = rs.getString(10);
				Date appdate = rs.getDate(11);
				String tmcategory= rs.getString(12);
				String imgFileName= rs.getString(13);
				String status= rs.getString(14);

				tm = new TradeMark();
				tm.setId(id);
				tm.setRegnumber(regnumber);
				tm.setTmtype(tmtype);
				tm.setApprovalnumber(approvalnumber);
				tm.setRegnoticenumber(regnoticenumber);
				tm.setValidstartdate(validstartdate);
				tm.setValidenddate(validenddate);
				tm.setApplicantname(applicantname);
				tm.setApplicantaddress(applicantaddress);
				tm.setAgent(agent);
				tm.setAppdate(appdate);
				tm.setTmcategory(tmcategory);
				tm.setImgFileName(imgFileName);
				tm.setStatus(status);

			} else {
				logger.info("数据库内没有符合条件的数据");

			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return tm;
	}
	
	
	// 获取商标注册号和商标的国际分类号
	public String getCorrectAppName(Integer custId,String appName) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select appName from  applicant_map where custId=? and errorName=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);
			pstmt.setString(2, appName);

			rs = pstmt.executeQuery();

			if (rs == null || !rs.next()) {
				return null;
			}
			if (rs.next()) {
				String correctName = rs.getString(1);
				return correctName;					
			} 
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
		
	}
	
	
	// 获取某个客户的所有申请人的商标数据
	public List<TradeMark> getCustomerTmList(Integer custId)
			throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<TradeMark> list = new ArrayList<TradeMark>();
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select id, regnumber, tmtype, approvalnumber, regnoticenumber, validstartdate, validenddate, applicantname, applicantaddress, agent, appdate, tmcategory, imgFileName, status from  trademark where custid=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);
			
			rs = pstmt.executeQuery();

			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {

					TradeMark tm = new TradeMark();
					// 获取regNumber
					int id= rs.getInt(1);
					String regnumber = rs.getString(2);
					String tmtype = rs.getString(3);
					String approvalnumber = rs.getString(4);
					String regnoticenumber = rs.getString(5);
					Date validstartdate = rs.getDate(6);
					Date validenddate = rs.getDate(7);
					String applicantname = rs.getString(8);
					String applicantaddress = rs.getString(9);
					String agent = rs.getString(10);
					Date appdate = rs.getDate(11);
					String tmcategory= rs.getString(12);
					String imgFileName= rs.getString(13);
					String status= rs.getString(14);
					
					
					tm.setId(id);
					tm.setRegnumber(regnumber);
					tm.setTmtype(tmtype);
					tm.setApprovalnumber(approvalnumber);
					tm.setRegnoticenumber(regnoticenumber);
					tm.setValidstartdate(validstartdate);
					tm.setValidenddate(validenddate);
					tm.setApplicantname(applicantname);
					tm.setApplicantaddress(applicantaddress);
					tm.setAgent(agent);
					tm.setAppdate(appdate);
					tm.setTmcategory(tmcategory);
					tm.setImgFileName(imgFileName);
					tm.setStatus(status);
					list.add(tm);

				} while (rs.next());
			} else {
				logger.info("数据库内没有符合条件的数据");

			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return list;
	}
	

	// 获取商标的流程数据
	public List<TradeMarkProcess> getTradeMarkProcessList(Integer tmid)
			throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<TradeMarkProcess> list = new ArrayList<TradeMarkProcess>();
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select status, statusdate  from  trademarkprocess where tmid=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, tmid);

			rs = pstmt.executeQuery();

			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {

					TradeMarkProcess tmp = new TradeMarkProcess();
					String status = rs.getString(1);
					Date statusdate = rs.getDate(2);
					tmp.setTmid(tmid);
					tmp.setStatus(status);
					tmp.setStatusdate(statusdate);

					list.add(tmp);

				} while (rs.next());
			} else {
				logger.info("数据库内没有符合条件的数据");

			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return list;
	}
	
	
	

	// 获取数据库中是否有某个商标的公告数据
	public List<JsonSbGongGao> getTradeMarkGongGao(String regNumber) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<JsonSbGongGao> gonggaos = new ArrayList<JsonSbGongGao>();
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select ggName, ggDate from  trademarkgonggao where regNumber=? order by ggDate DESC";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, regNumber);

			rs = pstmt.executeQuery();

			if (rs == null) {
				return null;
			}
			if (rs.next()) {
				do {

					String ggName = rs.getString(1);
					Date ggDate = rs.getDate(2);

					JsonSbGongGao jsonSbGongGao = new JsonSbGongGao();
					jsonSbGongGao.setGgName(ggName);
					jsonSbGongGao.setGgDate(ggDate);
					gonggaos.add(jsonSbGongGao);

				} while (rs.next());
			} else {
				return null;

			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return gonggaos;
	}

	
	
	// 删除某个商标的商品和服务数据
	public void deleteTradeMarkCategory(String regNumber) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "delete from trademarkcategory where regnumber=?";
			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, regNumber);				

			pstmt.executeUpdate();
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// 删除某个商标的流程数据
	public void deleteTradeMarkProcess(Integer tmid) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "delete from trademarkprocess where tmid=?";
			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, tmid);				

			pstmt.executeUpdate();
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// 粗粒度查找数据库中是否有某个商标的商品/服务信息
	public List<TradeMarkCategory> getTradeMarkCategory(String regNumber) throws Exception {
	
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<TradeMarkCategory> list=new ArrayList<TradeMarkCategory>();

		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select tmgroup, name from  trademarkcategory where regnumber=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, regNumber);

			rs = pstmt.executeQuery();

			if (rs == null) {
//					logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					TradeMarkCategory tmc= new TradeMarkCategory();
					String tmgroup = rs.getString(1);
					String name = rs.getString(2);
					tmc.setRegnumber(regNumber);
					tmc.setTmgroup(tmgroup);
					tmc.setName(name);
					
					list.add(tmc);

				} while (rs.next());
			} else {
//					logger.info("数据库内没有符合条件的数据");

			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return list;
	}
		

	
	// 细粒度查找数据库中是否有某个商标的商品/服务信息，要求regNumber与tmgroup也必须相同
	public boolean getCategoryWithTmGroup(String regNumber, String tmGroup, String name)
			throws Exception {
		boolean result = false;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select id from  trademarkcategory where regnumber=? and tmgroup=? and name=? ";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, regNumber);
			pstmt.setString(2, tmGroup);
			pstmt.setString(3, name);

			rs = pstmt.executeQuery();

			if (rs == null) {
				result = false;
			}
			if (rs.next()) {
				result = true;
			} else {
				result = false;

			}
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	
	

	// 获取客户商标数据更新的时间
	public Map<Integer, Date> getTradeMarkUpdateRecord(Integer custId, String appName, String opt) throws Exception {
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		Map<Integer, Date> updateRecord=new HashMap<Integer, Date>();
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select id, optDate from  trademarkupdate where custId=? and appName=? and opt=? order by optDate DESC";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);
			pstmt.setString(2, appName);
			pstmt.setString(3, opt);

			rs = pstmt.executeQuery();

			if (rs == null) {
				return null;
			}
			if (rs.next()) {
				do {
					int id=rs.getInt(1);
					Date date = rs.getDate(2);
					updateRecord.put(id, date);
					break;

				} while (rs.next());
			}

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return updateRecord;
	}

	
	
	

	// 获取客户商标数据更新的时间
	public String getTradeMarkUpdateStatus(Integer custId, String appName, String opt) throws Exception {
		String status = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select status from  trademarkupdate where custId=? and appName=? and opt=? order by optDate DESC";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);
			pstmt.setString(2, appName);
			pstmt.setString(3, opt);

			rs = pstmt.executeQuery();

			if (rs == null) {
				return null;
			}
			if (rs.next()) {
				do {
					status = rs.getString(1);
					break;

				} while (rs.next());
			}

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return status;
	}

	
	// 获取某个商标数据更新的时间
	public Date getTmDetailUpdateDate(Integer tmId) throws Exception {
		Date date = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select modifyDate from  trademark where id=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, tmId);
		
			rs = pstmt.executeQuery();

			if (rs == null) {
				return null;
			}
			if (rs.next()) {
				do {
					date = rs.getDate(1);
					break;

				} while (rs.next());
			}

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return date;
	}
	
	
	// 获取某个客户的商标数据更新的时间
	public Date geTmDataUpdateDate(Integer custId) throws Exception {
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Date date = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select optDate from  trademarkupdate where custId=? and opt=tm order by optDate DESC";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);			

			rs = pstmt.executeQuery();

			if (rs == null) {
				return null;
			}
			if (rs.next()) {
				do {
					date = rs.getDate(1);
					break;

				} while (rs.next());
			}

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return date;
	}
	
	
	
	
	// 获取某个客户的商标数据更新的数量
	public List<AppDataStatus> getApplicantTmStatus(Integer custId) throws Exception {
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<AppDataStatus> appDataStatusList =new ArrayList<AppDataStatus>();		

		try {
			con = DatabaseUtil.getConForHgj();
//			String query = "select applicantname, count(id) as count from  trademark where custId=? and modifyDate=? group by applicantname ";
			
			String query = "select a.applicantname, a.totalCount, b.updateCount, b.modifyDate "
					+ "from "
					+ "(select applicantname, count(id) as totalCount from trademark where 1=1 and custId=? group by applicantname ) a  "
					+ "left join (select applicantname, count(id) as updateCount, max(modifyDate) as modifyDate from trademark where 1=1 and custId=? and modifyDate is not null group by applicantname)  b "
					+ "on a.applicantname=b.applicantname";
			
			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);	
			pstmt.setInt(2, custId);	
//			String date = DateTool.getDate(modifyDate);
//			pstmt.setString(2, "" + date);		

			rs = pstmt.executeQuery();

			if (rs == null) {
				return null;
			}
			if (rs.next()) {
				do {
					String appName=rs.getString(1);
					int totalCount = rs.getInt(2);
					int updateCount = rs.getInt(3);
					Date modifyDate=rs.getDate(4);
					
					if (totalCount>0){
						AppDataStatus appDataStatus=new AppDataStatus();
						appDataStatus.setAppName(appName);	
						appDataStatus.setTotalCount(totalCount);
						appDataStatus.setUpdateCount(updateCount);
						appDataStatus.setModifyDate(modifyDate);
						
//						if (modifyDate==null){
//							Date date=new Date();							
//							appDataStatus.setModifyDate(date);							
//							System.out.println("modifyDate is null, set it with: "+ date );
//						}
						
						appDataStatusList.add(appDataStatus);	
					}

				} while (rs.next());
			}

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return appDataStatusList;
	}
	
	
	
	// 获取某个客户商标总数
	public TmDataStatus getCustomerTmStatus(Integer custId) throws Exception {
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		TmDataStatus tmDataStatus=new TmDataStatus();

		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select  a.totalTmCount, b.updateTmCount "
					+ "from "
					+ "(select custId, count(id) as totalTmCount from trademark where 1=1 and custId=?) a  "
					+ "left join (select custId, count(id) as updateTmCount from trademark where 1=1 and custId=? and modifyDate is not null)  b "
					+ "on a.custId=b.custId";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);	
			pstmt.setInt(2, custId);	
			rs = pstmt.executeQuery();

			if (rs == null) {
				return tmDataStatus;
			}
			if (rs.next()) {
				do {
					int totalTmCount = rs.getInt(1);
					int updateTmCount =rs.getInt(2);
					
					tmDataStatus.setTotalTmCount(totalTmCount);
					tmDataStatus.setUpdateTmCount(updateTmCount);
					break;	

				} while (rs.next());
			}

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return tmDataStatus;
	}
		
	

	// 将商标公告数据插入tradmarkgonggao表
	public void insertTradeMarkGongGaoTable(String regNumber ,List<JsonSbGongGao> ggList )
			throws Exception {

		Connection con = null;
		PreparedStatement pstmt = null;

//		String regNumber = jsonTmGongGaoInfo.getRegNumber();

		try {
			con = DatabaseUtil.getConForHgj();

			for (JsonSbGongGao gg : ggList) {

				String ggName = gg.getGgName();
				String ggQihao = gg.getGgQihao();
				String ggPage = gg.getGgPage();
				Date ggDate = gg.getGgDate();
//				
//				//先删除相同的公告期的公告
//				String delSql = "delete from trademarkgonggao where 1=1 and regNumber=? and ggQihao=?";
//				pstmt = (PreparedStatement) con.prepareStatement(delSql);
//				pstmt.setString(1, regNumber);
//				pstmt.setString(2, ggQihao);				
//				pstmt.execute();				
//				pstmt.close();
				
				//再插入当前公告
				String query = "insert into trademarkgonggao (regNumber, ggName, ggDate, ggQihao, ggPage) values (?, ?, ?, ?, ?)";
				pstmt = (PreparedStatement) con.prepareStatement(query);
				pstmt.setString(1, regNumber);
				pstmt.setString(2, ggName);
				if (ggDate == null) {
					pstmt.setString(3, null);
				} else {
					String date = DateTool.getDate(ggDate);
					pstmt.setString(3, "" + date);
				}
				pstmt.setString(4, ggQihao);
				pstmt.setString(5, ggPage);
				pstmt.execute();
				pstmt.close();
			}

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	
	


	// 将商标流程数据插入trademarkprocess表
	public void insertTradeMarkProcessTable(List<TradeMarkProcess> tmPs)
			throws Exception {

		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			for (TradeMarkProcess tmp : tmPs) {

				int tmid = tmp.getTmid();
				String status = tmp.getStatus();
				Date statusdate = tmp.getStatusdate();

				String query = "insert into trademarkprocess (" + "tmid, "
						+ "status, " + "statusdate )" + "values (?, ?, ?)";
				pstmt = (PreparedStatement) con.prepareStatement(query);
				pstmt.setInt(1, tmid);
				pstmt.setString(2, status);
				if (statusdate == null) {
					pstmt.setString(3, null);
				} else {
					String date = DateTool.getDate(statusdate);
					pstmt.setString(3, "" + date);
				}
				pstmt.execute();
				pstmt.close();
			}

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	
	
	// 将官网商标数据插入trademark表
	public void insertTradeMarkTable(TradeMark tm) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String regnumber = tm.getRegnumber();
			String tmtype = tm.getTmtype();			
			String tmname = tm.getTmname();
			String applicantname = tm.getApplicantname();		
			String status = tm.getStatus();
			Date appdate = tm.getAppdate();
			String approvalnumber = tm.getApprovalnumber();
			Date approvaldate = tm.getApprovaldate();
			String regnoticenumber = tm.getRegnoticenumber();
			Date regnoticedate = tm.getRegnoticedate();
			String tmcategory = tm.getTmcategory();
			String tmgroup = tm.getTmgroup();
			
			String agent = tm.getAgent();
			
			
			String tmimage = tm.getTmimage();
			String applicantaddress=tm.getApplicantaddress();
			String applicantEnName = tm.getApplicantenname();
			String applicantEnAddress=tm.getApplicantenaddress();
			String gtApplicantName=tm.getGtapplicantname();
			String gtApplicantAddress=tm.getGtapplicantaddress();
			String imgFileName=tm.getImgFileName();
			Date validStartDate = tm.getValidstartdate();
			Date validEndDate=tm.getValidenddate();
			String classify = tm.getClassify();
			
			// String country = tm.getCountry();
			// String tujing = tm.getTujing();

			
			String query = "insert into trademark"
					+ "("
					+ "regnumber, "
					+ "tmtype, "					
					+ "tmname, "
					+ "applicantname, "
					+ "status, "
					+ "appdate, "
					+ "approvalnumber, "
					+ "approvaldate, "
					+ "regnoticenumber, "
					+ "regnoticedate, "				
					+ "agent, "
					+ "modifydate,"
					+ "tmcategory,"
					+ "tmgroup,"
					+ "tmimage,"
					+ "applicantaddress,"
					+ "applicantEnName,"
					+ "applicantEnAddress,"
					+ "gtApplicantName,"
					+ "gtApplicantAddress,"
					+ "imgFileUrl,"
					+ "validStartDate,"
					+ "validEndDate,"
					+ "classify ) "
					+ "values ( ?, ?, ?, "
					+ "?, ?, ?, "
					+ "?, ?, ?, "
					+ "?, ?, ?, "
					+ "?, ?, ?, "
					+ "?, ?, ?, "
					+ "?, ?, ?, "
					+ "?, ?, ?)";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, regnumber);
			pstmt.setString(2, tmtype);
			pstmt.setString(3, tmname);
			pstmt.setString(4, applicantname);			
			pstmt.setString(5, status);

			if (appdate == null) {
				pstmt.setString(6, null);
			} else {
				String date = DateTool.getDate(appdate);
				pstmt.setString(6, date);
			}
			
			pstmt.setString(7, approvalnumber);
			
			if (approvaldate == null) {
				pstmt.setString(8, null);
			} else {
				String date = DateTool.getDate(approvaldate);
				pstmt.setString(8, date);
			}
			
			pstmt.setString(9, regnoticenumber);
			
			if (regnoticedate == null) {
				pstmt.setString(10, null);
			} else {
				String date = DateTool.getDate(regnoticedate);
				pstmt.setString(10, "" + date);
			}

			pstmt.setString(11, agent);		
//			pstmt.setString(12, imgFileName);		
			//设置修改时间
			Date now=new Date();
			String modifyDate = DateTool.getDate(now);
			pstmt.setString(12, "" + modifyDate);
			if (tmcategory!=null){
				int len = tmcategory.length();
				if (len > 2500) {
					logger.info("Data too long for  tmcategory: " + len);
					tmcategory = tmcategory.substring(0, 2500);
				}
			}
			pstmt.setString(13, tmcategory);
			pstmt.setString(14, tmgroup);
			pstmt.setString(15,tmimage);
			pstmt.setString(16, applicantaddress);
			pstmt.setString(17, applicantEnName);
			pstmt.setString(18, applicantEnAddress);
			pstmt.setString(19, gtApplicantName);
			pstmt.setString(20, gtApplicantAddress);
			pstmt.setString(21, imgFileName);
			if (validStartDate == null) {
				pstmt.setString(22, null);
			} else {
				String date = DateTool.getDate(validStartDate);
				pstmt.setString(22, "" + date);
			}

			if (validEndDate == null) {
				pstmt.setString(23, null);
			} else {
				String date = DateTool.getDate(validEndDate);
				pstmt.setString(23, "" + date);
			}
			pstmt.setString(24, classify);
			
			
			pstmt.execute();
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 将官网商标数据插入trademark表
		public Integer selectIdByRegNumberAndType(String regNumber,String tmType) throws Exception {
			Connection con = null;
			PreparedStatement pstmt = null;
			Integer tmId=null;
			try {
				con = DatabaseUtil.getConForHgj();
				
				String query = "select tmId from  trademark where regNumber= ? and tmType = ? ";

				pstmt = (PreparedStatement) con.prepareStatement(query);
				pstmt.setString(1, regNumber);
				pstmt.setString(2, tmType);
				ResultSet rs = pstmt.executeQuery();
				while(rs.next()){
					tmId=rs.getInt(1);
				}
				pstmt.close();

			} catch (java.lang.ClassNotFoundException e) {
				throw e;
			} catch (SQLException ex) {
				throw ex;
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					if (pstmt != null) {
						pstmt.close();
					}
					if (con != null) {
						con.close();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return tmId ;
		}
		
	

	// 将商品和服务数据插入TradeMarkCategory表
	public void insertTradeMarkCategoryTable(
			List<TradeMarkCategory> tradeMarkCategoryList) throws Exception {

		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			for (TradeMarkCategory tmc : tradeMarkCategoryList) {

				String regNumber = tmc.getRegnumber();

				String name = tmc.getName();
				int No = tmc.getNo();
				String tmGroup = tmc.getTmgroup();
				Integer tmType = tmc.getTmtype();
				Integer tmId = tmc.getTmId();

				String query = "insert into trademarkcategory (name, no, tmgroup, tmtype, regnumber ) "
						+ "values (?, ?, ?, ?, ?)";
				pstmt = (PreparedStatement) con.prepareStatement(query);
				if (name!=null){
					int len = name.length();
					if (len > 400) {
						logger.info(" Data too long for column name:" + len);
						name = name.substring(0, 400);
					}
				}
				pstmt.setString(1, name);
				pstmt.setInt(2, No);
				pstmt.setString(3, tmGroup);
				pstmt.setInt(4, tmType);
				pstmt.setString(5, regNumber);
//				pstmt.setInt(6, tmId);
				pstmt.execute();
				pstmt.close();
			}
		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	
	
	// 更新trademark表中的官网商标图片
	public void updateTradeMarkImage(Integer id, String tmimage)
			throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "update  trademark set tmimage=?, modifyDate=? where tmId=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, tmimage);
			//设置修改时间
			Date now=new Date();
			String modifyDate = DateTool.getDate(now);
			pstmt.setString(2, "" + modifyDate);
			pstmt.setInt(3, id);
			
			pstmt.executeUpdate();
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// 更新trademark表中的官网商标状态
	public void updateTradeMarkStatus(Integer id, String status, String imgFileName)
			throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "update  trademark set status=?, imgFileName=? where id=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, status);
			pstmt.setString(2, imgFileName);
			
			pstmt.setInt(3, id);
			
			pstmt.executeUpdate();
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	// 更新trademark表中的官网商标图片
		public void updateTradeMarkModifyData(Integer id)
				throws Exception {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = DatabaseUtil.getConForHgj();

				String query = "update  trademark set  modifyDate=? where tmId=?";

				pstmt = (PreparedStatement) con.prepareStatement(query);
				
				//设置修改时间
				Date now=new Date();
				String modifyDate = DateTool.getDate(now);
				pstmt.setString(1, "" + modifyDate);
				pstmt.setInt(2, id);
				
				pstmt.executeUpdate();
				pstmt.close();

			} catch (java.lang.ClassNotFoundException e) {
				throw e;
			} catch (SQLException ex) {
				throw ex;
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					if (pstmt != null) {
						pstmt.close();
					}
					if (con != null) {
						con.close();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	

	// 更新trademark表中的官网商标数据
	public void updateTradeMarkTable(TradeMark tm, boolean updateImage) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "update trademark "
					+ "set applicantname=?, applicantaddress=?, applicantenname=?, applicantenaddress=?, gtapplicantname=?, "
					+ "status=?, appdate=?, approvalnumber=?, approvaldate=?, "
					+ "regnoticenumber=?, regnoticedate=?, "
					+ "validstartdate=?, validenddate=?, tmcategory=?, tmgroup=?, agent=?, imgFileUrl=?, modifyDate=? "
					+ "where tmId=? ";
			
			if(updateImage){
				 query = "update trademark "
						+ "set applicantname=?, applicantaddress=?, applicantenname=?, applicantenaddress=?, gtapplicantname=?, "
						+ "status=?, appdate=?, approvalnumber=?, approvaldate=?, "
						+ "regnoticenumber=?, regnoticedate=?, "
						+ "validstartdate=?, validenddate=?, tmcategory=?, tmgroup=?, agent=?, imgFileUrl=?, modifyDate=?, tmimage=? "
						+ "where tmId=? ";
			}

			pstmt = (PreparedStatement) con.prepareStatement(query);

			int id = tm.getId();
			
			String applicantname = tm.getApplicantname();
			String applicantaddress = tm.getApplicantaddress();
			String applicantenname = tm.getApplicantenname();
			String applicantenaddress=tm.getApplicantenaddress();
			String gtapplicantname=tm.getGtapplicantname();
			
			
			String status = tm.getStatus();
			Date appdate = tm.getAppdate();
			String approvalnumber = tm.getApprovalnumber();
			Date approvaldate = tm.getApprovaldate();
			String regnoticenumber = tm.getRegnoticenumber();
			Date regnoticedate = tm.getRegnoticedate();
			Date validstartdate = tm.getValidstartdate();
			Date validenddate = tm.getValidenddate();
			String tmcategory = tm.getTmcategory();
			String tmgroup=tm.getTmgroup();
			String agent = tm.getAgent();
			String tmimage=tm.getTmimage();
			String imgFileName=tm.getImgFileName();

			pstmt.setString(1, applicantname);
			pstmt.setString(2, applicantaddress);
			pstmt.setString(3, applicantenname);
			pstmt.setString(4, applicantenaddress);
			pstmt.setString(5, gtapplicantname);
			pstmt.setString(6, status);

			if (appdate == null) {
				pstmt.setString(7, null);
			} else {
				String date = DateTool.getDate(appdate);
				pstmt.setString(7, date);
			}

			pstmt.setString(8, approvalnumber);

			if (approvaldate == null) {
				pstmt.setString(9, null);
			} else {
				String date = DateTool.getDate(approvaldate);
				pstmt.setString(9, date);
			}

			pstmt.setString(10, regnoticenumber);

			if (regnoticedate == null) {
				pstmt.setString(11, null);
			} else {
				String date = DateTool.getDate(regnoticedate);
				pstmt.setString(11, "" + date);
			}

			if (validstartdate == null) {
				pstmt.setString(12, null);
			} else {
				String date = DateTool.getDate(validstartdate);
				pstmt.setString(12, "" + date);
			}

			if (validenddate == null) {
				pstmt.setString(13, null);
			} else {
				String date = DateTool.getDate(validenddate);
				pstmt.setString(13, "" + date);
			}
			if (tmcategory!=null){
				int len = tmcategory.length();
				if (len > 2500) {
					logger.info("Data too long for  tmcategory: " + len);
					tmcategory = tmcategory.substring(0, 2500);
				}
			}
			pstmt.setString(14, tmcategory);
			pstmt.setString(15, tmgroup);
			pstmt.setString(16, agent);
			
			pstmt.setString(17, imgFileName);
			
			//设置修改时间
			Date now=new Date();
			String modifyDate = DateTool.getDate(now);
			pstmt.setString(18, "" + modifyDate);
			
			if(!updateImage){
				pstmt.setInt(19, id);
			}else{
				pstmt.setString(19, tmimage);
				pstmt.setInt(20, id);
			}
			

			pstmt.executeUpdate();
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	// 更新trademark表中的商标图片数据
	public void updateTmImage(Integer id, String tmImage) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "update trademark set tmimage=? where id=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);

			pstmt.setString(1, tmImage);
			pstmt.setInt(2, id);				

			pstmt.executeUpdate();
			pstmt.close();

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}




	
	// 将对客户的数据更新操作记录插入表中
	public void insertTradeMarkUpdateTable(Integer custId, String appName, String opt)
			throws Exception {

		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = DatabaseUtil.getConForHgj();
			

			// 先删除记录
			String sql = "delete from trademarkupdate where appName=? and opt=? and custId=?";
			pstmt = (PreparedStatement) con.prepareStatement(sql);
			pstmt.setString(1, appName);
			pstmt.setString(2, opt);
			pstmt.setInt(3, custId);
			pstmt.execute();
			pstmt.close();

			// 再插入记录
			String query = "insert into trademarkupdate (custId, appName, opt, optDate) values (?, ?, ?, ?)";
			pstmt = (PreparedStatement) con.prepareStatement(query);	
					
			pstmt.setInt(1, custId);
			pstmt.setString(2, appName);
			pstmt.setString(3, opt);
			Date optDate = new Date();
			String date = DateTool.getDateTime(optDate);
			pstmt.setString(4, "" + date);
			pstmt.execute();			

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	
	// 更新trademarkupdate表中的状态数据
	public void updateTmUpdateRecord(Integer custId, String appName, String status, String opt)
			throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		boolean insertData=false;
		try {
			
			Date optDate = new Date();
			String date = DateTool.getDateTime(optDate);
			
			
			con = DatabaseUtil.getConForHgj();
			
			String sql = "select id, optDate from  trademarkupdate where appName=? and opt=?  and custId=? order by optDate DESC";

			pstmt = (PreparedStatement) con.prepareStatement(sql);
			pstmt.setString(1, appName);
			pstmt.setString(2, opt);
			pstmt.setInt(3, custId);

			rs = pstmt.executeQuery();

			if (rs == null || !rs.next()) {
				insertData=true;
			}
			rs.close();
			pstmt.close();
			
			if (insertData){
				sql = "insert into trademarkupdate (custId, appName, opt, optDate, status) values (?, ?, ?, ?, ?)";
				pstmt = (PreparedStatement) con.prepareStatement(sql);	
				pstmt.setInt(1, custId);
				pstmt.setString(2, appName);
				pstmt.setString(3, opt);				
				pstmt.setString(4, "" + date);
				pstmt.setString(5, status);		
				pstmt.execute();	
			}else{				
				sql = "update  trademarkupdate set status=?, optDate=? where appName=? and opt=? and custId=? ";
				pstmt = (PreparedStatement) con.prepareStatement(sql);
				pstmt.setString(1, status);					
				pstmt.setString(2, "" + date);
				pstmt.setString(3, appName);	
				pstmt.setString(4, opt);
				pstmt.setInt(5, custId);
				pstmt.executeUpdate();	
			}
	
		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	// 将新的申请人信息插入到数据库。
	public static void insertApplicantTable(Integer custId, List<String> appNames) throws Exception{
		Connection con = null;		
		PreparedStatement pstmt = null;
	
        try{
        	con = DatabaseUtil.getConForHgj();
    	
			Date date = new Date();			
			String modifyTime=DateTool.getDateTime(date);
			
			Integer appType= 1;
			
			for (String appName: appNames){
						
				String query = "insert into applicant (appName, inputDate, inputId, modifyDate, modifyId, custId, appType) "
						+ "values (?, ?, ?, ?, ?, ?, ?)";

				pstmt = (PreparedStatement) con.prepareStatement(query);				
				pstmt.setString(1, appName);				
				
				if (modifyTime == null) {
					pstmt.setString(2, null);
				} else {
					pstmt.setString(2,  modifyTime);
				}				
				pstmt.setString(3, "admin");			
				
				if (modifyTime == null) {
					pstmt.setString(4, null);
				} else {
					pstmt.setString(4,  modifyTime);
				}
				
				pstmt.setString(5, "admin");
				pstmt.setInt(6, custId);			
				pstmt.setInt(7, appType);
				pstmt.execute();
				pstmt.close();
				logger.info("insert appName:"+ appName);

			}
       
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (pstmt != null) {
					if (!pstmt.isClosed())
						pstmt.close();
				}
				if (con != null) {
					if (!con.isClosed())
						con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	public static void main(String[] args) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
