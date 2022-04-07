package chat.vo;

import java.io.Serializable;



public class UserVO implements Serializable{

	private String userId;
	private String userPw;
	private String userName;
	
	public UserVO(String userId, String userPw, String userName) {
		this.userId =userId;
		this.userPw = userPw;
		this.userName =userName;
	}
	
	public UserVO() {
		
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
	
	
	  @Override
	public String toString() {
		return "UserVO [userId=" + userId + ", userPw=" + userPw + ", userName=" + userName + "]";
	}

	@Override
	    public boolean equals(Object object) {
	        if (this == object) return true;
	        if (object == null || getClass() != object.getClass()) return false;
	        UserVO userVo = (UserVO) object;
	        return getUserId().equals(userVo.getUserId());
	    }
}
