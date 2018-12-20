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
import java.util.StringTokenizer;

import com.tmkoo.searchapi.vo.MonitorGongGao;
import com.tmkoo.searchapi.vo.MonitorTm;
import com.tmkoo.searchapi.vo.Users;

public class MonitorGgDbService extends DataService {

	// 获取客户的代理人信息
	public static List<Users> getUserList(Integer custId) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Users> list = new ArrayList<Users>();
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select d.username, d.email from users d, agent_customer e "
					+ "where 1=1 and e.custId=? and e.userId=d.userId "
					+ "union "
					+ "select c.username, c.email  from users c "
					+ "where  c.username in  "
					+ "(select a.parentUser from users a,agent_customer b where b.custId=? and b.userId=a.userId) "
					+ "union "
					+ "select f.username, f.email "
					+ "from users f,users_departments g "
					+ "where g.deptId=1 and f.userId=g.userId and f.userType=22";

			pstmt = (PreparedStatement) con.prepareStatement(query);

			pstmt.setInt(1, custId);
			pstmt.setInt(2, custId);

			rs = pstmt.executeQuery();

			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {

					String username = rs.getString(1);
					String email = rs.getString(2);
					Users user = new Users();
					user.setUsername(username);
					user.setEmail(email);
					list.add(user);

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

	// 获取公告中的商标注册号
	public static List<MonitorGongGao> getGgList(int ggQihao) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<MonitorGongGao> list = new ArrayList<MonitorGongGao>();
		try {
			con = DatabaseUtil.getConForHgj();
			
			//获取没有执行过更新的商标数据
			//因为，从公告中没有获取商标名称，只有等调用过标库接口后，才修改了商标名称。
			//所以，当获取没有调用过标库接口的商标时，查询条件加上tmNameCn is null
			String query = "select id, regNumber, tmType from monitorgonggao where ggQihao=? and tmNameCn is null";

			pstmt = (PreparedStatement) con.prepareStatement(query);

			pstmt.setInt(1, ggQihao);

			rs = pstmt.executeQuery();

			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {

					int id = rs.getInt(1);
					String regNumber = rs.getString(2);
					String tmType = rs.getString(3);

					MonitorGongGao gg = new MonitorGongGao();
					gg.setId(id);
					gg.setRegNumber(regNumber);
					gg.setTmType(tmType);

					list.add(gg);

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

	// 获取某个商标类别
	public static String getMonitorTmType(Integer custId, String regNumber)
			throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String tmTypes = null;

		try {

			con = DatabaseUtil.getConForHgj();

			String query = "select tmtype from  monitortm where custid=?  and regnumber=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, custId);
			pstmt.setString(2, regNumber);
			rs = pstmt.executeQuery();

			if (rs == null) {
				logger.info("数据库内没有符合条件的数据");
			}
			if (rs.next()) {
				do {
					String tmType = rs.getString(1);
					if (tmTypes == null) {
						tmTypes = tmType;
					} else {
						tmTypes = tmTypes + "," + tmType;
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

		return tmTypes;
	}

	// 粗粒度查找数据库中是否有某个商标的商品/服务信息
	public static boolean getGgFromDb(String regNumber, String ggDate)
			throws Exception {
		boolean result = false;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select id from monitorgonggao where regNumber=? and ggDate=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);

			pstmt.setString(1, regNumber);
			pstmt.setString(2, ggDate);

			rs = pstmt.executeQuery();

			if (rs == null || !rs.next()) {
				result = false;
			} else {
				result = true;
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

	public static void insertMonitorGongGaoTable(List<MonitorGongGao> list)
			throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DatabaseUtil.getConForHgj();

			for (MonitorGongGao gongGao : list) {

				String regNumber = gongGao.getRegNumber();
				String tmType = gongGao.getTmType();
				Date ggDate = gongGao.getGgDate();
				// 增加公告期号，公告页
				int ggQihao = gongGao.getGgQihao();
				int ggPage = gongGao.getGgPage();

				String ggFilePath = gongGao.getGgFilePath();
				int locationPage = gongGao.getLocationPage();
				String ggFileUrl = gongGao.getGgFileUrl();

				// 判断数据库中是否有相同的公告记录
				String query = "select id from monitorgonggao  where regNumber=? and ggQihao=? and ggPage=?";
				pstmt = (PreparedStatement) con.prepareStatement(query);
				pstmt.setString(1, regNumber);
				pstmt.setInt(2, ggQihao);
				pstmt.setInt(3, ggPage);
				rs = pstmt.executeQuery();
				if (rs != null && rs.next()) {
					pstmt.close();
					String update = "update monitorgonggao  set ggFileUrl = ? , locationPage=? where regNumber=? and ggQihao=? and ggPage=? and ggFileUrl is null";
					pstmt = (PreparedStatement) con.prepareStatement(update);
					pstmt.setString(1, ggFileUrl);
					pstmt.setInt(2, locationPage);
					pstmt.setString(3, regNumber);
					pstmt.setInt(4, ggQihao);
					pstmt.setInt(5, ggPage);
					pstmt.execute();
					pstmt.close();
					continue;
				}
				pstmt.close();

				// 如果数据库中没有相同的公告记录，执行插入操作
				String sql = "insert into monitorgonggao (regNumber, tmType, ggDate, ggQihao, ggPage, ggFilePath, locationPage, ggFileUrl)"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				pstmt = (PreparedStatement) con.prepareStatement(sql);

				pstmt.setString(1, regNumber);
				
				if (tmType==null || tmType.equals("")){
					System.out.println("tmType is null "); 
					continue;
				}
				int len=tmType.length();
				if (len>150){
					tmType=tmType.substring(0,150);
					System.out.println("Data too long for column 'tmType': "+ len );
				}
				pstmt.setString(2,  tmType);
				if (ggDate == null) {
					pstmt.setString(3, null);
				} else {
					String date = DateTool.getDate(ggDate);
					pstmt.setString(3, date);
				}
				pstmt.setInt(4, ggQihao);
				pstmt.setInt(5, ggPage);
				pstmt.setString(6, ggFilePath);
				pstmt.setInt(7, locationPage);
				pstmt.setString(8, ggFileUrl);

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

	// 更新monitorgonggao表中的商标数据
	public void updateMonitorGongGao(MonitorGongGao tm) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "update MonitorGongGao "
					+ "set tmNameCn=?, tmNameEn=?, tmPinyin=?, "
					+ "tmGroup=?, tmImage=?, imageLink=?, "
					+ "appName=?,  agent=?, monitorType=?, imagePath=? " + "where id=? ";

			pstmt = (PreparedStatement) con.prepareStatement(query);

			int id = tm.getId();

			String tmNameCn = tm.getTmNameCn();
			String tmNameEn = tm.getTmNameEn();
			String tmPinyin = tm.getTmPinyin();
			String tmGroup = tm.getTmGroup();
			String tmImage = tm.getTmImage();
			String imageLink = tm.getImageLink();
			String appName = tm.getAppName();
			String agent = tm.getAgent();
			Integer monitorType = tm.getMonitorType();
			String imagePath = tm.getImagePath();
			int len1=tmNameCn.length();
			if (len1>100){
				tmNameCn=tmNameCn.substring(0,100);
				System.out.println("Data too long for column 'tmNameCn': "+ len1 );
			}
			
			pstmt.setString(1, tmNameCn);
			pstmt.setString(2, tmNameEn);
			pstmt.setString(3, tmPinyin);
			
			int len2=tmGroup.length();
			if (len2 >300){
				tmGroup=tmGroup.substring(0,300);
				System.out.println("Data too long for column 'tmGroup': "+ len2 );
			}
			pstmt.setString(4, tmGroup);
			
			pstmt.setString(5, tmImage);
			pstmt.setString(6, imageLink);
			pstmt.setString(7, appName);
			pstmt.setString(8, agent);
			pstmt.setInt(9, monitorType);
			pstmt.setString(10, imagePath);
			pstmt.setInt(11, id);

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

	// 判断商标图片是否为空
	public boolean imageIsNull(String regNumber) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select id from  monitorgonggao where regNumber=? and tmImage is null";

			pstmt = (PreparedStatement) con.prepareStatement(query);

			pstmt.setString(2, regNumber);

			rs = pstmt.executeQuery();

			if (rs == null || !rs.next()) {
				return false;
			} else {
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
	
	//查询imagePath转换为tmImage
	public static List<MonitorGongGao> getImagePath() throws Exception {
		boolean result = false;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<MonitorGongGao> ggs = new ArrayList<MonitorGongGao>();
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "select id,imagePath from monitorgonggao where tmimage is null";

			pstmt = (PreparedStatement) con.prepareStatement(query);

			rs = pstmt.executeQuery();

			while(rs.next()) {
				MonitorGongGao mgg = new MonitorGongGao();
				mgg.setId(rs.getInt(1));
				mgg.setImagePath(rs.getString(2));
				ggs.add(mgg);
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

		return ggs;
	}
	
	// 更新monitorgonggao表中的tmimage商标数据
	public static void updateTmImage(MonitorGongGao tm) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "update MonitorGongGao set tmImage=? where id=? ";

			pstmt = (PreparedStatement) con.prepareStatement(query);

			int id = tm.getId();
			String tmImage = tm.getTmImage();
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
	
	// 更新ggqihao表中的数据
	public void updateGgqihao(Integer qihao) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DatabaseUtil.getConForHgj();

			String query = "update ggqihao set qihao=?";

			pstmt = (PreparedStatement) con.prepareStatement(query);
			pstmt.setInt(1, qihao);
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
	//查询imagePath转换为tmImage
		public static Map<String,Integer> getMonitorGongGaoId(int qiHao) throws Exception {
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			Map<String,Integer> mm = new HashMap<String, Integer>();
			try {
				con = DatabaseUtil.getConForHgj();

				String query = "select max(id) maxId,min(id) minId from monitorgonggao where ggQihao= ? and imagepath is not null";

				pstmt = (PreparedStatement) con.prepareStatement(query);
				pstmt.setInt(1, qiHao);
				rs = pstmt.executeQuery();

				while(rs.next()) {
					mm.put("max",rs.getInt(1));
					mm.put("min",rs.getInt(2));
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

			return mm;
		}
}
