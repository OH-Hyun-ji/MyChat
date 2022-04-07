package chat.server;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.*;

import chat.db.UserDB;
import chat.protocol.Action;
import chat.protocol.MyException;
import chat.protocol.Protocol;
import chat.vo.ChatVO;
import chat.vo.JoinVO;
import chat.vo.LoginVO;
import chat.vo.UserVO;
import chat.vo.WhisperVO;

public class MultiServerThread implements Runnable{

	private MultiServer multiServer; //�������� 
	private Socket socket ;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private List<UserVO> userVoList;
	private UserVO userVo;
	private UserDB userDb = new UserDB();

	public MultiServerThread(MultiServer multiServer) {
		this.multiServer = multiServer;
		userVoList=multiServer.getJoinUserList();
	}
	
	public MultiServerThread(Socket socket) {
		this.socket =socket;
		

	}
	//������ ����
	public synchronized void run() {
		boolean isStop= false;
			socket = multiServer.getSocket(); //������ ���� ���� �޾ƿ��� 
			
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				
			}catch (IOException e) {
				e.printStackTrace();
			}
			
			Protocol protocol = null;
			while(!isStop) {
				try {
					protocol = (Protocol) ois.readObject();
					
					switch(protocol.getAction()) {
					
						case LOGIN:login(protocol.getObject()); break;
						case JOIN: join(protocol.getObject()); break;
						case CHAT: chatting(protocol.getObject()); break;
						case MODIFY: userModify(protocol.getObject()); break;
						case DELETE: userDelete(protocol.getObject()); break;
						case ID_CHECK: userIdCheck(protocol.getObject()); break;
						case EXIT : userExit(protocol.getObject()); break;
						case FILE_SEND_REQUSET : fileSend(protocol.getObject()); break;
						case MODIFY_BASIC : modifyBasic(protocol.getObject()); break;
					
						}
				
					
				}catch(MyException e) {
					try {
						sendError(e.getMessage());
					}catch(IOException ex) {
						ex.printStackTrace();
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					System.out.println("e1����");
				} 
	
	
				 catch (Exception e) {
					removeUser();
					isStop = true;
					e.printStackTrace();
					
					try {
						socket.close();
					e.printStackTrace();
					
					}catch(IOException e1) {
						e.printStackTrace();
					
					}
					
				}
			}	
	

	
}

		private void modifyBasic(Object object) throws IOException {
			if(object instanceof String id) {
				UserVO vo= userDb.findId(id);
				
				Protocol protocol = new Protocol(Action.MODIFY_BASIC_OK, vo);
				oos.writeObject(protocol);
			}
		
	}

		private void fileSend(Object object) {
		
		
	}

		private void sendError(String errorMessage) throws IOException {
		Protocol protocol = new Protocol(Action.ERROR, errorMessage);
		oos.writeObject(protocol);
	}

		private synchronized void userExit(Object object) {
			if (object instanceof ChatVO chatVo) {
				// multiServer.getJoinUserList().stream().forEach(id -> checkAndRemoveId(chatVo, id));
				try {
					//checkAndRemoveId(chatVo, userVo);
					multiServer.getJoinUserList().removeIf(userVO -> userVO.getUserId().equals(chatVo.getWriterId()));
					//removeIf()��  ���ڷ� ���޵��������� ����Ʈ�� �����۵��� �����Ѵ�. ���ǿ� ���յǴ°� ����!! �׷����ʴ°��� �����!!
	            
					List<String> idList = multiServer.getJoinUserList().stream().map(UserVO::getUserId).toList(); 
					
					// ���� �ҽ�
					Protocol protocol = new Protocol(Action.CLIENT_EXITIST, idList);
									
					for(MultiServerThread multiServerThread : multiServer.getLoginServerList() ) {
						multiServerThread.sendMsg(protocol);
					}
					/////////////////////////////////////////////
					//�����ʿ� ����.. ������ ������ ������ Ŭ���̾�Ʈ�� �������� �Ǿ� �ֿ�
//					oos.writeObject(new Protocol(Action.CLIENT_EXITIST, idList)); // ���� �ҽ� �ش� Ŭ���̾�Ʈ���� ����
					//stream �� �÷���,�迭� ���� ����Ǿ��ִ� ��ҵ��� �ϳ��� �����Ͽ� �ݺ����� ó���� �����ϰ� �ϴ� ��� 
					multiServer.setServerUserList(idList);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				multiServer.getServerMemo().add(chatVo.getWriterId() + "���� �����Ͽ����ϴ�.!" + System.getProperty("line.separator"));
				
				
			}
			
		}
	
	  /*  private void checkAndRemoveId (ChatVO chatVo, UserVO id) {
	        if (id.getUserId().equals(chatVo.getWriterId())) {
	            multiServer.getJoinUserList().remove(id);
	        }
	    }*/

	    
	    
	private void userIdCheck(Object object) throws IOException {
		String id = new String();
		id = (String) object;
		
		if(userDb.findId(id) != null ) {
			oos.writeObject(new Protocol(Action.ID_CHECK_FAIL,null));
		}else if(id.length()==0) {
			oos.writeObject(new Protocol(Action.ID_CHECK_ZERO,null));
		}
		else {
		oos.writeObject(new Protocol(Action.ID_CHECK_SUCCESS,null));
		}
	}


	private void userDelete(Object object) throws IOException {
		if(object instanceof String id){
		userDb.deleteUser(id);
		oos.writeObject(new Protocol(Action.DELETE_OK,null));
		}
	}
	private void userModify(Object object) throws IOException {
		if(object instanceof UserVO) {
			UserVO modifyU = (UserVO) object;
			userDb.modifyUser(modifyU);
			oos.writeObject(new Protocol(Action.MODIFY_OK,null));
		
		}
		
	}

	//�α���===========================================================================
	private void login(Object object) throws Exception {
		if(object instanceof LoginVO loginVo) {
			UserVO findUser = userDb.findId(loginVo.getUserId());
			
			checkLogin(loginVo,findUser);
			checkAlreadyLogin(findUser);
			successLogin(findUser);
	
			return;
		}
		
	}
	
	
	private void checkAlreadyLogin(UserVO findUser) throws MyException {
		if(userVoList.contains(findUser)) {
		//	JOptionPane.showMessageDialog(null, "�̹� ������ ������Դϴ�...","Login_fail",JOptionPane.WARNING_MESSAGE);
			throw new MyException("�̹� ������ ������Դϴ�. �ٽ� �α������ּ���");
		}
	}
	
	private void successLogin(UserVO findUser) throws IOException {
		userVoList.add(findUser);
		userVo = findUser;
		
		Protocol protocol = new Protocol(Action.LOGIN_SUCCESS, null);
		oos.writeObject(protocol);
		
		multiServer.getLoginServerList().add(this);
		protocol = new Protocol(Action.CHANGE_USER_LIST, userVoList);
		multiServer.getServerMemo().add("�α����� ����ڼ� :" + multiServer.getLoginServerList().size());
		
		for(MultiServerThread multiServerThread : multiServer.getLoginServerList() ) {
			multiServerThread.sendMsg(protocol);
		}
		multiServer.getServerUserList().add(userVo.getUserId()); //������ ������ ���
		
	}
	private void checkLogin(LoginVO loginVo,UserVO findUser) throws MyException {
		if(findUser == null) {
			//JOptionPane.showMessageDialog(null, "����");
			throw new MyException("�α��� ����, ���̵� ����ġ!!!");
		}
		if(!findUser.getUserPw().equals(loginVo.getUserPw())) {
			throw new MyException("�α��� ����, ��й�ȣ ����ġ!!!");
		}
		
	}
	
	//ȸ������=========================================================
	private synchronized void join(Object object) throws IOException, MyException { //ȸ�������� �ش������ڿ��� �ٻ���ϰ� ���;��ϹǷ�  synchronized
		if(object instanceof JoinVO) {
			JoinVO joinVo = (JoinVO) object;
			checkId(joinVo.getUserId());
			userDb.join(joinVo);
			Protocol protocol = new Protocol(Action.JOIN_SUCCESS, null);
			oos.writeObject(protocol);
			return;
		}
		
		throw new MyException("�߸��� �� �Դϴ�.");
	}
	
	//id�ߺ�üũ ======================================================
	private void checkId(String id) throws MyException  {
		UserVO findUser = userDb.findId(id);
	
		if(findUser != null) {
			throw new MyException("ȸ������ ����, �̹� �����ϴ� ���̵��Դϴ�.");
		}
	}

		
	//ä�� ==========================================================
	private void chatting(Object object) throws IOException {
		if(object instanceof ChatVO) {
			ChatVO chatVo = (ChatVO) object;
			Protocol protocol = new Protocol(Action.CHAT,chatVo);
			multiServer.getServerMemo().add(chatVo.getWriterId() + ": "+ chatVo.getMessage());
			
			
			
			if(chatVo.getWhisperId() != null) {
				for(MultiServerThread serverThread : multiServer.getLoginServerList()) {
					
					if(serverThread.getUserVo().getUserId().equals(chatVo.getWhisperId())) { //���������������� �ּҷ� ã�ư��°� 
						
						serverThread.sendMsg(new Protocol(Action.WHISPER,new WhisperVO(chatVo.getWriterId(),chatVo.getWhisperId(),chatVo.getMessage())));
						//������ �ƴ϶� Ŭ�󳢸� �������ϴϱ� ���������������� ��������, �ȱ׷� ���ڽ����׸� ���δ�....��
						
						
						String message = chatVo.getMessage();
						String target = chatVo.getWhisperId();
						message = target+"�Բ� ���� �ӼӸ�: "+message;
						chatVo.setMessage(message);
						oos.writeObject(new Protocol(Action.CHAT,chatVo));
						return;
					}
					
				}
			}
			for(MultiServerThread serverThread :multiServer.getLoginServerList()) {
				serverThread.sendMsg(protocol);
			}
			return;	
		}
	}

	//���� �����Ҷ� ====================================================
	private void removeUser() {
		Protocol protocol;
		multiServer.getLoginServerList().remove(this);
		multiServer.getServerThreadList().remove(this);
		
		//multiServer.getServerMemo().add(userVo.getUserId()+"���� �����Ͽ����ϴ�.111");
		protocol = new Protocol(Action.CHANGE_USER_LIST, userVoList);
		for(MultiServerThread serverThread: multiServer.getLoginServerList()) {
		
			
			try {
			serverThread.sendMsg(protocol);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendMsg(Protocol protocol) throws IOException {
		oos.writeObject(protocol);
		oos.reset();
		
		
	}

	
	
	public UserVO getUserVo() {
		return userVo;
	}

}
