package com.tmkoo.searchapi.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import org.apache.log4j.Logger;


public class DataService {

	public static Logger logger = Logger.getLogger(DataService.class);


	public static void deleteDateFromTable(String tableName)
			throws Exception {
		Connection con = null;
		Statement statement = null;

		try {
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();		
			String sql = "truncate table "+  tableName;
			statement.executeUpdate(sql);
			

		} catch (java.lang.ClassNotFoundException e) {
			throw e;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	
	
	//获取所有监控商标的注册号和类别
	public static Map<String,String> getRegNumberAndType() throws Exception {

		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		Map<String,String> regNumberAndType = new HashMap<String,String>();
		try {

			//
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();
			String selectSql = " select regNumber,tmType from monitortmtype ";
			rs = statement.executeQuery(selectSql);
			if (rs == null) {
				throw new RuntimeException("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					// 获取appId
					String regNumber = rs.getString(1);
					String TmType = rs.getString(2);
					regNumberAndType.put(regNumber, TmType);
				} while (rs.next());
			} else {
				throw new RuntimeException("数据库内没有符合条件的数据");

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
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return regNumberAndType;
	}
	//根据注册号和类别获取监控商标的状态值
	public static String getStatus(String regNumber,String tmType) throws Exception {

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String status=null;
		try {
			con = DatabaseUtil.getConForHgj();
			String query = " select status from monitortmtype where regNumber=? and tmType=? ";
			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setString(1, regNumber);
			pstmt.setString(2, tmType);
			rs = pstmt.executeQuery();
			if (rs == null) {
				throw new RuntimeException("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				// 获取appId
				status = rs.getString(1);
			} else {
				throw new RuntimeException("数据库内没有符合条件的数据");

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

	public static List<String> getCustNames() throws Exception {

		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		try {

			//
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();
			String selectSql = "select distinct(custName) from customer";
			rs = statement.executeQuery(selectSql);
			if (rs == null) {
				throw new RuntimeException("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					
					String custName = rs.getString(1);
					list.add(custName);
				} while (rs.next());
			} else {
				throw new RuntimeException("数据库内没有符合条件的数据");

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
				if (statement != null) {
					statement.close();
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
	
	
	
	//获取客户名称
	public static String getCustomerName(Integer custId) throws Exception {

		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		String custName = null;
		try {

			//
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();
			String selectSql = "select custName from customer where custId="+ custId;
			rs = statement.executeQuery(selectSql);
			if (rs == null) {
				throw new RuntimeException("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					// 获取appId
					custName = rs.getString(1);
				    break;
				} while (rs.next());
			} else {
				throw new RuntimeException("数据库内没有符合条件的数据");

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
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return custName;
	}
	
	

	public static Map<Integer, Integer> getCustomerInfo() throws Exception {

		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		Map<Integer, Integer> custIds = new HashMap<Integer, Integer>();
		try {
		
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();
			String selectSql = "select custId, wpmCustId from customer";
			rs = statement.executeQuery(selectSql);
			if (rs == null) {
				throw new RuntimeException("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					// 获取appId
					int hgjCustId = rs.getInt(1);
					int wpmcustId = rs.getInt(2);
					custIds.put(hgjCustId, wpmcustId);
				} while (rs.next());
			} else {
				throw new RuntimeException("数据库内没有符合条件的数据");

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
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return custIds;
	}
	
	
	public Map<String, Integer> getCustInfo() throws Exception {

		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		Map<String, Integer> custIds = new HashMap<String, Integer>();
		try {
		
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();
			String selectSql = "select custId, custName from customer";
			rs = statement.executeQuery(selectSql);
			if (rs == null) {
				throw new RuntimeException("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					// 获取appId
					int custId = rs.getInt(1);
					String custName = rs.getString(2);
					custIds.put(custName,custId);
				} while (rs.next());
			} else {
				throw new RuntimeException("数据库内没有符合条件的数据");

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
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return custIds;
	}

	
	
	public static Map<Integer, String> getApplicantInfo(int custId,
			String condition) throws Exception {

		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		Map<Integer, String> applicants = new HashMap<Integer, String>();

		try {

			//
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();
			// 当appType为1，意味着申请人是商标权利人，
			String selectSql = "select appId, appName from applicant where custId="
					+ custId + condition;
			rs = statement.executeQuery(selectSql);
			if (rs == null) {
				throw new RuntimeException("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					// 获取appId
					int id = rs.getInt(1);
					String appName = rs.getString(2);
					applicants.put(id, appName);
				} while (rs.next());
			} else {
				logger.info("数据库内没有符合条件的数据");

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
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return applicants;
	}
	
	
	//获取申请人名称
	public static List<String> getAppNames(int custId) throws Exception {

		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		List<String> applicants = new ArrayList<String>();

		try {

			//
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();
			// 当appType为1，意味着申请人是商标权利人，
			String selectSql = "select appName from applicant where appType=1 and custId="
					+ custId;
			rs = statement.executeQuery(selectSql);
			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					String appName = rs.getString(1);
					
					if (appName!=null){
					
						if (appName.indexOf("（")>-1){
							appName=appName.replaceAll("（", "(");
						}
						if (appName.indexOf("）")>-1){
							appName=appName.replaceAll("）", ")");
						}
					}
					
					applicants.add(appName);
				} while (rs.next());
			} else {
				logger.info("数据库内没有符合条件的数据");

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
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return applicants;
	}
	
	
	
	//获取申请人名称
	public static List<String> getAppNameFromTm(int custId) throws Exception {

		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		List<String> applicants = new ArrayList<String>();

		try {

			//
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();
			// 当appType为1，意味着申请人是商标权利人，
			String selectSql = "select distinct(applicantname) from trademark where custId="
					+ custId;
			rs = statement.executeQuery(selectSql);
			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					String appName = rs.getString(1);
					
					applicants.add(appName);
				} while (rs.next());
			} else {
				logger.info("数据库内没有符合条件的数据");

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
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return applicants;
	}
	
	
	
	//获取申请人名称
	public static List<String> getTmAppNames(int custId) throws Exception {

		Connection con = null;
		Statement statement = null;
		ResultSet rs = null;
		List<String> applicants = new ArrayList<String>();

		try {

			//
			con = DatabaseUtil.getConForHgj();
			statement = con.createStatement();
			// 当appType为1，意味着申请人是商标权利人，
			String selectSql = "select distinct(applicantname) from trademark where appType=1 and custId="
					+ custId;
			rs = statement.executeQuery(selectSql);
			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					String appName = rs.getString(1);
					
					applicants.add(appName);
				} while (rs.next());
			} else {
				logger.info("数据库内没有符合条件的数据");

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
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return applicants;
	}


	public static void updateHgjDb() {
		try {
			// logger.info("start insert applicant table");
			// insertApplicantData();
			// logger.info("end insert applicant table");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	public static List<Integer> checkParam(String []args, List<Integer> custIds){
		
		
		if (args==null || args.length==0){
			return custIds;
		}
					
		List<Integer> usedCustIds= new ArrayList<Integer>();
		for (String idstr: args){
			if (!StringUtils.isNum(idstr)){	
				logger.info("custId参数必须是数字");				
				continue;						
			}
			int id = Integer.parseInt(idstr);
			boolean find = false;	
			// 检查输入的参数中的custId是否符合
			for(Integer hgjCustId:custIds){
				int custid= hgjCustId.intValue();
				if (id==custid){
					find = true;
					break;
				}
			}	
			// 如果参数不符合，那么直接返回。
			if (!find){
				logger.info("custId参数必须是32，33，34，39，40，41，42,...中的任何一个或者几个");
				
				break;					
			}						
			usedCustIds.add(id);
			
		}				
		
	
		return usedCustIds;
	}

	public static void main(String[] args) {
		try {	

			updateHgjDb();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
