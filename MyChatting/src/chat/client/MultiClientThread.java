package chat.client;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import chat.protocol.Protocol;
import chat.vo.ChatVO;
import chat.vo.UserVO;
import chat.vo.WhisperVO;


public class MultiClientThread extends Thread {

	private MultiClient multiClient;
	private ObjectInputStream ois;
	
	public MultiClientThread(MultiClient multiClient) {
		this.multiClient= multiClient;
		this.ois = multiClient.getOis();
	}
	public void run() {
		Protocol protocol =null;
		boolean isStop =false;
		while(!isStop) {
			try {
				protocol = (Protocol) ois.readObject();
			
				switch(protocol.getAction()) {
				
					case LOGIN_SUCCESS: loginSuccess(protocol); break;//따로 가지고올게 없음
					case ID_CHECK_FAIL : idCheckFail();break;
					case ID_CHECK_SUCCESS : idCheckSuccess();break;
					case ID_CHECK_ZERO : idCheckFailZero();break;
					case JOIN_SUCCESS : joinSuccess(protocol);break;
					case CHAT : updateCheckList(protocol);break;
					case CHANGE_USER_LIST : updateUserList(protocol);break;
					case JOIN_FAIL : joinFail(protocol); break;
					case WHISPER :whisper(protocol); break;
					case CLIENT_EXITIST :clientExit(protocol);break;
					case ERROR : sendError(protocol);
					case MODIFY_BASIC_OK :modifyVo(protocol);break;
					case DELETE_OK: userDelete(protocol); break;
					case MODIFY_OK: modifyOK(); break;
				//	case EXIT_SUCCESS : System.exit(0);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				isStop =true;
				
				JOptionPane.showMessageDialog(null,"서버 종료!!!","Server Finish",JOptionPane.CLOSED_OPTION); //알림창
				System.exit(0);
			}
		}
		
	}
	
	private void modifyOK() {
		JOptionPane.showMessageDialog(null, "회원정보가 수정완료 되었습니다.","MODIFY_SUCCESS",JOptionPane.OK_OPTION);
		
	}
	private void userDelete(Protocol protocol) {
		JOptionPane.showMessageDialog(null, "회원정보가 삭제완료 되었습니다.","DELETE_SUCCESS",JOptionPane.OK_OPTION);
		
	}
	private void modifyVo(Protocol protocol) {
		UserVO vo = (UserVO) protocol.getObject();
		multiClient.getModiId().setText(vo.getUserId());
		multiClient.getModiPw().setText(vo.getUserPw());
		multiClient.getModiCheckPw().setText(vo.getUserPw());
		multiClient.getModiName().setText(vo.getUserName());
	
		
	}
	private void sendError(Protocol protocol) {
		String errorMessage = (String) protocol.getObject();
		JOptionPane.showMessageDialog(null, errorMessage,"error",JOptionPane.ERROR_MESSAGE);
	}
	//나갔을때 클라이언트쪽 접속자목록 
	private synchronized void clientExit(Protocol protocol)  {
		List<String> list = (List<String>) protocol.getObject();

		multiClient.setUserList(list);
		
	//	List<String> UserList = list.stream().map(UserVO::getUserId).toList();
		
		
	}
	// 귓속말==========================================
	private void whisper(Protocol protocol) {
		WhisperVO whisperVo = (WhisperVO) protocol.getObject();
		multiClient.getJta().append(whisperVo.getWhisperToId()+"님의 귓속말 : "+whisperVo.getWhisperMsg()
		+ System.getProperty("line.separator"));
		
		
		
		
	}
	private void joinFail(Protocol protocol) {
		multiClient.joinFail();
		
	}
	//아이디 중복확인 빈칸
	private void idCheckFailZero() {
		multiClient.idCheckFailZero();
		
	}
	//아이디 중복확인 실패 ====================================================================
	private void idCheckFail() {
		multiClient.idCheckFail();
	}
	//아이디 중복확인 성공 ====================================================================
	private void idCheckSuccess() {
		multiClient.idCheckSuccess();
	}
	//채팅 입력시 ===========================================================================
	private void updateCheckList(Protocol protocol) {
		ChatVO chatVo = (ChatVO) protocol.getObject();
		
		multiClient.getJta().append(chatVo.getWriterId() + ":"+chatVo.getMessage()+
				System.getProperty("line.separator")); //시스템마다 다른 줄개행을 알아서 맞춰줌
		
		multiClient.getJta().setCaretPosition(multiClient.getJta().getDocument().getLength());
		
	}
	
	//접속한 유저 목록 업데이트 =========================================
	private void updateUserList(Protocol protocol) {
		List<UserVO> userVoList = (List<UserVO>) protocol.getObject();
		
		List<String> idList = userVoList.stream().map(UserVO::getUserId).toList(); // UserVO에서 이름만 가져오기 
		
		multiClient.setJoinUserList(idList);
	}
	
	//회원가입 성공했을 경우 ============================================
	private void joinSuccess(Protocol protocol) {
		multiClient.successJoin();
	}
	//로그인 성공했을 경우 ==============================================
	private void loginSuccess(Protocol protocol) {
		multiClient.successLogin();
	}
	
	
	

}
