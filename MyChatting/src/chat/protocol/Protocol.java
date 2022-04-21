package chat.protocol;

import java.io.Serializable;

public class Protocol  implements Serializable{
	private Action action; 
	private Object object;

	public Protocol(Action action ,Object object) {
		this.action = action;
		this.object = object;
	}
	
	public Action getAction() {
		return action;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	
}
