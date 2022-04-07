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

	private MultiServer multiServer; //서버정보 
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
	//스레드 역할
	public synchronized void run() {
		boolean isStop= false;
			socket = multiServer.getSocket(); //서버로 부터 소켓 받아오고 
			
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
					System.out.println("e1에러");
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
					//removeIf()는  인자로 전달된조건으로 리스트의 아이템들을 삭제한다. 조건에 부합되는건 삭제!! 그렇지않는것은 남긴다!!
	            
					List<String> idList = multiServer.getJoinUserList().stream().map(UserVO::getUserId).toList(); 
					
					// 변경 소스
					Protocol protocol = new Protocol(Action.CLIENT_EXITIST, idList);
									
					for(MultiServerThread multiServerThread : multiServer.getLoginServerList() ) {
						multiServerThread.sendMsg(protocol);
					}
					/////////////////////////////////////////////
					//서버쪽에 보면.. 삭제를 했을때 삭제한 클라이언트에 보내도록 되어 있움
//					oos.writeObject(new Protocol(Action.CLIENT_EXITIST, idList)); // 기존 소스 해당 클라이언트에만 전송
					//stream 은 컬렉션,배열등에 대해 저장되어있는 요소들을 하나씩 참조하여 반복적인 처리를 가능하게 하는 기능 
					multiServer.setServerUserList(idList);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				multiServer.getServerMemo().add(chatVo.getWriterId() + "님이 퇴장하였습니다.!" + System.getProperty("line.separator"));
				
				
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

	//로그인===========================================================================
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
		//	JOptionPane.showMessageDialog(null, "이미 접속한 사용자입니다...","Login_fail",JOptionPane.WARNING_MESSAGE);
			throw new MyException("이미 접속한 사용자입니다. 다시 로그인해주세요");
		}
	}
	
	private void successLogin(UserVO findUser) throws IOException {
		userVoList.add(findUser);
		userVo = findUser;
		
		Protocol protocol = new Protocol(Action.LOGIN_SUCCESS, null);
		oos.writeObject(protocol);
		
		multiServer.getLoginServerList().add(this);
		protocol = new Protocol(Action.CHANGE_USER_LIST, userVoList);
		multiServer.getServerMemo().add("로그인한 사용자수 :" + multiServer.getLoginServerList().size());
		
		for(MultiServerThread multiServerThread : multiServer.getLoginServerList() ) {
			multiServerThread.sendMsg(protocol);
		}
		multiServer.getServerUserList().add(userVo.getUserId()); //서버쪽 접속자 목록
		
	}
	private void checkLogin(LoginVO loginVo,UserVO findUser) throws MyException {
		if(findUser == null) {
			//JOptionPane.showMessageDialog(null, "서벙");
			throw new MyException("로그인 실패, 아이디 불일치!!!");
		}
		if(!findUser.getUserPw().equals(loginVo.getUserPw())) {
			throw new MyException("로그인 실패, 비밀번호 불일치!!!");
		}
		
	}
	
	//회원가입=========================================================
	private synchronized void join(Object object) throws IOException, MyException { //회원가입은 해당정보자원을 다사용하고 나와야하므로  synchronized
		if(object instanceof JoinVO) {
			JoinVO joinVo = (JoinVO) object;
			checkId(joinVo.getUserId());
			userDb.join(joinVo);
			Protocol protocol = new Protocol(Action.JOIN_SUCCESS, null);
			oos.writeObject(protocol);
			return;
		}
		
		throw new MyException("잘못된 값 입니다.");
	}
	
	//id중복체크 ======================================================
	private void checkId(String id) throws MyException  {
		UserVO findUser = userDb.findId(id);
	
		if(findUser != null) {
			throw new MyException("회원가입 실패, 이미 존재하는 아이디입니다.");
		}
	}

		
	//채팅 ==========================================================
	private void chatting(Object object) throws IOException {
		if(object instanceof ChatVO) {
			ChatVO chatVo = (ChatVO) object;
			Protocol protocol = new Protocol(Action.CHAT,chatVo);
			multiServer.getServerMemo().add(chatVo.getWriterId() + ": "+ chatVo.getMessage());
			
			
			
			if(chatVo.getWhisperId() != null) {
				for(MultiServerThread serverThread : multiServer.getLoginServerList()) {
					
					if(serverThread.getUserVo().getUserId().equals(chatVo.getWhisperId())) { //지목한유저쓰레드 주소로 찾아가는것 
						
						serverThread.sendMsg(new Protocol(Action.WHISPER,new WhisperVO(chatVo.getWriterId(),chatVo.getWhisperId(),chatVo.getMessage())));
						//서버가 아니라 클라끼리 보내야하니까 서버스레드쪽으로 보내야함, 안그럼 나자신한테만 보인다....ㅠ
						
						
						String message = chatVo.getMessage();
						String target = chatVo.getWhisperId();
						message = target+"님께 보낸 귓속말: "+message;
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

	//연결 종료할때 ====================================================
	private void removeUser() {
		Protocol protocol;
		multiServer.getLoginServerList().remove(this);
		multiServer.getServerThreadList().remove(this);
		
		//multiServer.getServerMemo().add(userVo.getUserId()+"님이 퇴장하였습니다.111");
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
