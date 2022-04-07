package chat.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import chat.vo.JoinVO;
import chat.vo.UserVO;

public class UserDB {

	private int count = 0;
	private Connection con =null; //������ ���̽��� �����ϴ� ��ü
    private Statement st = null;        //�׳� �����°�
    private PreparedStatement ps =null; //? �־ �ִ°�
    private ResultSet rs =null;   // ������ ������ ���� �޴°�ü
	
	
	public UserDB() {
		try {
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String id = "SYSTEM";
			String pw = "spring";
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			con = DriverManager.getConnection(url, id, pw);
			System.out.println("DB  ���� �Ϸ�!!!");
			
		}catch(ClassNotFoundException e) {
			System.out.println("DB ����̹� �ε� ���� : " + e.toString());
		}catch(SQLException e1) {
			System.out.println("DB ���� ���� : " + e1.toString());
		}catch(Exception e2) {
			e2.printStackTrace();
		}

	}

	  public void join(JoinVO joinVo) {
		  try {
		  String sql = "insert into user_table values(?,?,?)";
		  
		  ps =con.prepareStatement(sql);
		  
		  ps.setString(1,joinVo.getUserId());
		  ps.setString(2,joinVo.getUserPw());
		  ps.setString(3,joinVo.getUserName());
		  
		  count = ps.executeUpdate(); //������ϰ� �ٷ� ���� ����, intŸ���� ���� ��ȯ 


          if(count <=0){
              throw new Exception("ȸ�����Կ� �����߽��ϴ�.");
          }
          System.out.println("ȸ������ ���� !");
          
		  }catch (Exception e) {
			e.printStackTrace();
		}
         dbClose();

  }
		  
		  
	  
	
	  public UserVO findId(String id) {

	        String sql = "SELECT * FROM user_table WHERE userId = ?";
	        try {
	            ps =con.prepareStatement(sql);
	            ps.setString(1, id); // ?�� �ε��� 1���� ���� ,�Ű�����
	            rs = ps.executeQuery(); //�������� resultset ��ü�� ���� ��ȯ(int �� string ������� �׳� ����� ��ȯ)
	           
	           
	           if(rs.next()) {
	                UserVO userVo = new UserVO();
	                userVo.setUserId(rs.getString("USERID"));
	                userVo.setUserPw(rs.getString("USERPW"));
	                userVo.setUserName(rs.getString("USERNAME"));
	              
	                return userVo;
	           }else if(id.length() ==0 ) {
	        	   System.out.println("���̵� ��ĭ�� ");
	        	   return null;
	           }
	            return null; //���̵� ����

	        }catch(Exception e) {
	            e.printStackTrace();
	        }
	        dbClose();
	        return null; //DB����
	    }
	  
	
	  public void modifyUser (UserVO userVo) {  //ȸ������ ���� 

		 String sql = "update user_table set userPw = ?, userName = ? where userId = ? " ;
		 
		 try {
			
			ps= con.prepareStatement(sql);
			ps.setString(1,userVo.getUserPw());
			ps.setString(2, userVo.getUserName());
			ps.setString(3, userVo.getUserId());
			
			if(ps.executeUpdate() <=0) {
				System.out.println("���� ����");
			}
			
			System.out.println("�����Ϸ�!");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 dbClose();
	  }
	  
	  
	  public void deleteUser(String id) {
		  
		  String sql = "delete user_table where userId = ?";
		  
		  try {
			  
			ps = con.prepareStatement(sql);
			ps.setString(1, id);
			
			if(ps.executeUpdate() >0) { //�ȿ� ������ �߸� ����ɼ��� ������ ����ó���� ���ؼ� if�����
										 /*������ �Ǿ��µ� ���ϰ��� 0�̸� ���̺� �����Ͱ� ���ٴ¶� 
										  * 1�� ���ϵȰ� �����Ͱ� id ,1���� 1���� ������ �Ǵ°� ���� 					
										  */
				System.out.println("���� ����");
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		  
		  
		  dbClose();
	  }




	    //������� ������ �ڿ���������� ���� �ݾ��ֱ�
	    public void dbClose() {
	        try {
	            if(rs != null) rs.close();
	            if(st != null) st.close();
	            if(ps != null) ps.close();

	        }catch (Exception e) {
	            System.out.println(e + "=> dbClose fail");
	        }

	    }
}
