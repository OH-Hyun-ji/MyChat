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
	private Connection con =null; //데이터 베이스와 연결하는 객체
    private Statement st = null;        //그냥 가져온거
    private PreparedStatement ps =null; //? 넣어서 넣는것
    private ResultSet rs =null;   // 실행한 쿼리문 값을 받는객체
	
	
	public UserDB() {
		try {
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String id = "SYSTEM";
			String pw = "spring";
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			con = DriverManager.getConnection(url, id, pw);
			System.out.println("DB  연결 완료!!!");
			
		}catch(ClassNotFoundException e) {
			System.out.println("DB 드라이버 로딩 실패 : " + e.toString());
		}catch(SQLException e1) {
			System.out.println("DB 접속 실패 : " + e1.toString());
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
		  
		  count = ps.executeUpdate(); //저장안하고 바로 값을 실행, int타입의 값을 반환 


          if(count <=0){
              throw new Exception("회원가입에 실패했습니다.");
          }
          System.out.println("회원가입 성공 !");
          
		  }catch (Exception e) {
			e.printStackTrace();
		}
         dbClose();

  }
		  
		  
	  
	
	  public UserVO findId(String id) {

	        String sql = "SELECT * FROM user_table WHERE userId = ?";
	        try {
	            ps =con.prepareStatement(sql);
	            ps.setString(1, id); // ?의 인덱스 1부터 시작 ,매개변수
	            rs = ps.executeQuery(); //수행결과로 resultset 객체의 값을 반환(int 나 string 상관없이 그냥 결과값 반환)
	           
	           
	           if(rs.next()) {
	                UserVO userVo = new UserVO();
	                userVo.setUserId(rs.getString("USERID"));
	                userVo.setUserPw(rs.getString("USERPW"));
	                userVo.setUserName(rs.getString("USERNAME"));
	              
	                return userVo;
	           }else if(id.length() ==0 ) {
	        	   System.out.println("아이디 빈칸임 ");
	        	   return null;
	           }
	            return null; //아이디 없음

	        }catch(Exception e) {
	            e.printStackTrace();
	        }
	        dbClose();
	        return null; //DB오류
	    }
	  
	
	  public void modifyUser (UserVO userVo) {  //회원가입 수정 

		 String sql = "update user_table set userPw = ?, userName = ? where userId = ? " ;
		 
		 try {
			
			ps= con.prepareStatement(sql);
			ps.setString(1,userVo.getUserPw());
			ps.setString(2, userVo.getUserName());
			ps.setString(3, userVo.getUserId());
			
			if(ps.executeUpdate() <=0) {
				System.out.println("수정 실패");
			}
			
			System.out.println("수정완료!");
			
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
			
			if(ps.executeUpdate() >0) { //안에 쿼리가 잘못 실행될수도 있으니 예외처리를 위해서 if절사용
										 /*삭제는 되었는데 리턴값이 0이면 테이블에 데이터가 없다는뜻 
										  * 1로 리턴된건 데이터가 id ,1개라서 1개만 삭제가 되는게 정상 					
										  */
				System.out.println("삭제 성공");
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		  
		  
		  dbClose();
	  }




	    //사용하지 않을땐 자원낭비방지를 위해 닫아주기
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
