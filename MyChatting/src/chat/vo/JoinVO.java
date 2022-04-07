package chat.vo;

import java.io.Serializable;

public class JoinVO implements Serializable{

	private String userId;
	private String userPw;
	private String userPwCk;
	private String userName;
	
	
public JoinVO(String id, String pw, String name) {
		this.userId =id;
		this.userPw =pw;
		this.userName=name;
	}
	
	public JoinVO() {
		
	}
	
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserPw() {
		return userPw;
	}
	
	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}
	
	public String getUserName() {
		return userName;
	}

	 
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwCk() {
		return userPwCk;
	}

	
	public void setUserPwCk(String userPwCk) {
		this.userPwCk = userPwCk;
	}

	@Override
	public String toString() {
		return "JoinVO [userId=" + userId + ", userPw=" + userPw + ", userPwCk=" + userPwCk + ", userName=" + userName
				+ "]";
	}

	
	
	
}
