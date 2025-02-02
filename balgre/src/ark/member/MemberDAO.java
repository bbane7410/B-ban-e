package ark.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import ark.dbcp.DBConnectionMgr;

public class MemberDAO {

	private DBConnectionMgr pool;

	public MemberDAO( ) {
		try { pool = DBConnectionMgr.getInstance( ); } catch(Exception e) { e.printStackTrace( ); }
	}
	
	public boolean checkId(String id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		boolean flag = false;
		
		try {
			con = pool.getConnection( );
			sql = "select id from tblMember where id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			flag = pstmt.executeQuery().next();
		} 
		catch(Exception e) { e.printStackTrace( ); } 
		finally { pool.freeConnection(con, pstmt, rs); }
		return flag;
	}
	
	public Vector<ZipcodeBean> zipcodeRead(String area3) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		Vector<ZipcodeBean> vlist = new Vector<ZipcodeBean>( );
		
		try {
			con = pool.getConnection( );
			sql = "select * from tblZipcode where area3 like ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "%" + area3 + "%");
			rs = pstmt.executeQuery( );
			
			while (rs.next( )) {
				ZipcodeBean bean = new ZipcodeBean( );
				bean.setZipcode(rs.getString(1));
				bean.setArea1(rs.getString(2));
				bean.setArea2(rs.getString(3));
				bean.setArea3(rs.getString(4));
				bean.setArea4(rs.getString(5));
				vlist.add(bean);
			}
		}catch(Exception e) { e.printStackTrace( ); }finally { pool.freeConnection(con, pstmt, rs); }
		return vlist;
	}
	
	public boolean insertMember(MemberBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		
		try {
			con = pool.getConnection( );
			sql = "insert tblMember(id,pass,name,sex,birthday,email,zipcode,address,hobby,job)"
					+ "values(?,?,?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bean.getId( ));
			pstmt.setString(2, bean.getPass( ));
			pstmt.setString(3, bean.getName( ));
			pstmt.setString(4, bean.getSex( ));
			pstmt.setString(5, bean.getBirthday( ));
			pstmt.setString(6, bean.getEmail( ));
			pstmt.setString(7, bean.getZipcode( ));
			pstmt.setString(8, bean.getAddress( ));
			String hobby[] = bean.getHobby( );
			char hb[ ] = { '0', '0', '0', '0', '0' };
			String lists[ ] = { "인터넷", "여행", "게임", "영화", "운동" };
			for (int i = 0; i < hobby.length; i++) {
				for (int j = 0; j < lists.length; j++) {
					if (hobby[i].equals(lists[j]))
						hb[j] = '1';
				}
			}
			pstmt.setString(9, new String(hb));
			pstmt.setString(10, bean.getJob( ));
			if (pstmt.executeUpdate( ) == 1) flag = true;
		}catch(Exception e) { e.printStackTrace( ); } finally { pool.freeConnection(con, pstmt); }
		return flag;
	}
	
	public boolean loginMember(String id, String pass) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		boolean flag = false;
		
		try {
			con = pool.getConnection( );
			sql = "select id from tblMember where id=? and pass=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pass);
			rs = pstmt.executeQuery( );
			flag = rs.next();
		}catch(Exception e) { e.printStackTrace( ); } finally { pool.freeConnection(con, pstmt, rs); }
		return flag;
	}	
	
	public MemberBean getMember(String id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MemberBean bean = null;
		
		try {
			con = pool.getConnection( );
			String sql = "select * from tblMember where id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery( );
			
			if (rs.next( )) {
				bean = new MemberBean( );
				bean.setId(rs.getString("id"));
				bean.setPass(rs.getString("pass"));
				bean.setName(rs.getString("name"));
				bean.setSex(rs.getString("sex"));
				bean.setBirthday(rs.getString("birthday"));
				bean.setEmail(rs.getString("email"));
				bean.setZipcode(rs.getString("zipcode"));
				bean.setAddress(rs.getString("address"));
				String hobbys[] = new String[5];
				String hobby = rs.getString("hobby");
				for (int i = 0; i < hobbys.length; i++) {
					hobbys[i] = hobby.substring(i, i + 1);
				}
				bean.setHobby(hobbys);
				bean.setJob(rs.getString("job"));
			}
		} catch(Exception e) { e.printStackTrace( ); } finally { pool.freeConnection(con); }
		return bean;
	}
	
	public boolean updateMember(MemberBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		try {
			con = pool.getConnection( );
			String sql = "update tblMember set pass=?,name=?,sex=?,birthday=?,"
					+ "email=?,zipcode=?,address=?,hobby=?,job=? where id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bean.getPass( ));
			pstmt.setString(2, bean.getName( ));
			pstmt.setString(3, bean.getSex( ));
			pstmt.setString(4, bean.getBirthday( ));
			pstmt.setString(5, bean.getEmail( ));
			pstmt.setString(6, bean.getZipcode( ));
			pstmt.setString(7, bean.getAddress( ));
			char hobby[] = { '0', '0', '0', '0', '0' };
			if (bean.getHobby( ) != null) {
				String hobbys[ ] = bean.getHobby( );
				String list[ ] = { "인터넷", "여행", "게임", "영화", "운동" };
				for (int i = 0; i < hobbys.length; i++) {
					for (int j = 0; j < list.length; j++)
						if (hobbys[i].equals(list[j]))
							hobby[j] = '1';
				}
			}
			pstmt.setString(8, new String(hobby));
			pstmt.setString(9, bean.getJob( ));
			pstmt.setString(10, bean.getId( ));
			int count = pstmt.executeUpdate( );
			if (count > 0)
				flag = true;
		} catch (Exception e) { e.printStackTrace( ); } 	finally { pool.freeConnection(con, pstmt); }
		return flag;
	}
	
	public boolean deleteMember(String id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		try {
			con = pool.getConnection( );
			String sql = "delete from tblMember where id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);			
			int count = pstmt.executeUpdate( );
			if (count > 0) flag = true;
		} catch (Exception e) { e.printStackTrace( ); } 	finally { pool.freeConnection(con, pstmt); }
		return flag;
	}

}