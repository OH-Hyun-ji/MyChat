package chat.vo;

import java.io.Serializable;

public class LoginVO implements Serializable {
	
	private String userId;
	private String userPw;
	
	public LoginVO(String userId, String userPw) {
		this.userId = userId;
		this.userPw = userPw;
	}
	
	public LoginVO() {
		
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
	
	

}
