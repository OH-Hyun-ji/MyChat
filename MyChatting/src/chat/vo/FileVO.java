package chat.vo;

import java.util.Arrays;

public class FileVO {

	
	private String code;
	private int size;
	private byte[] data = new byte[1024];
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "FileVO [code=" + code + ", size=" + size + ", data=" + Arrays.toString(data) + "]";
	}
	
	
}
