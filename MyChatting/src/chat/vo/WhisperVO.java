package chat.vo;

import java.io.Serializable;

public class WhisperVO implements Serializable {

	private String whisperToId;
	private String whisperFromId;
	private String whisperMsg;
	
	public WhisperVO(String whisperToId, String whisperFromId, String whisperMsg) {
		this.whisperFromId=whisperFromId;
		this.whisperToId =whisperToId;
		this.whisperMsg = whisperMsg;
	}
	
	
	public WhisperVO() {
		
	}
	
	
	/**
	 * @return the whisperMsg
	 */
	public String getWhisperMsg() {
		return whisperMsg;
	}


	/**
	 * @param whisperMsg the whisperMsg to set
	 */
	public void setWhisperMsg(String whisperMsg) {
		this.whisperMsg = whisperMsg;
	}


	public String getWhisperToId() {
		return whisperToId;
	}
	
	public void setWhisperToId(String whisperToId) {
		this.whisperToId = whisperToId;
	}
	
	public String getWhisperFromId() {
		return whisperFromId;
	}
	
	public void setWhisperFromId(String whisperFromId) {
		this.whisperFromId = whisperFromId;
	}
	
	
}
