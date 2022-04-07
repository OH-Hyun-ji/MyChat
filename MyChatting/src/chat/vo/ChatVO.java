package chat.vo;

import java.io.Serializable;

public class ChatVO implements Serializable{

	private String message;
	private String writerId;
	private String whisperId; //내가 지목한 사람을 넣을 객체 
	
	public ChatVO(String writerId, String whisperId,String message) {
		this.writerId=writerId;
		this.whisperId=whisperId;
		this.message =message;
	}

	public ChatVO() {
		
	}
	

	public String getWhisperId() {
		return whisperId;
	}

	
	public void setWhisperId(String whisperId) {
		this.whisperId = whisperId;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getWriterId() {
		return writerId;
	}
	
	public void setWriterId(String writerId) {
		this.writerId = writerId;
	}
	
	
}
