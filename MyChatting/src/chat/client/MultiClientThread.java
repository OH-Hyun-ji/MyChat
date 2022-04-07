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
				
					case LOGIN_SUCCESS: loginSuccess(protocol); break;//���� ������ð� ����
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
				
				JOptionPane.showMessageDialog(null,"���� ����!!!","Server Finish",JOptionPane.CLOSED_OPTION); //�˸�â
				System.exit(0);
			}
		}
		
	}
	
	private void modifyOK() {
		JOptionPane.showMessageDialog(null, "ȸ�������� �����Ϸ� �Ǿ����ϴ�.","MODIFY_SUCCESS",JOptionPane.OK_OPTION);
		
	}
	private void userDelete(Protocol protocol) {
		JOptionPane.showMessageDialog(null, "ȸ�������� �����Ϸ� �Ǿ����ϴ�.","DELETE_SUCCESS",JOptionPane.OK_OPTION);
		
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
	//�������� Ŭ���̾�Ʈ�� �����ڸ�� 
	private synchronized void clientExit(Protocol protocol)  {
		List<String> list = (List<String>) protocol.getObject();

		multiClient.setUserList(list);
		
	//	List<String> UserList = list.stream().map(UserVO::getUserId).toList();
		
		
	}
	// �ӼӸ�==========================================
	private void whisper(Protocol protocol) {
		WhisperVO whisperVo = (WhisperVO) protocol.getObject();
		multiClient.getJta().append(whisperVo.getWhisperToId()+"���� �ӼӸ� : "+whisperVo.getWhisperMsg()
		+ System.getProperty("line.separator"));
		
		
		
		
	}
	private void joinFail(Protocol protocol) {
		multiClient.joinFail();
		
	}
	//���̵� �ߺ�Ȯ�� ��ĭ
	private void idCheckFailZero() {
		multiClient.idCheckFailZero();
		
	}
	//���̵� �ߺ�Ȯ�� ���� ====================================================================
	private void idCheckFail() {
		multiClient.idCheckFail();
	}
	//���̵� �ߺ�Ȯ�� ���� ====================================================================
	private void idCheckSuccess() {
		multiClient.idCheckSuccess();
	}
	//ä�� �Է½� ===========================================================================
	private void updateCheckList(Protocol protocol) {
		ChatVO chatVo = (ChatVO) protocol.getObject();
		
		multiClient.getJta().append(chatVo.getWriterId() + ":"+chatVo.getMessage()+
				System.getProperty("line.separator")); //�ý��۸��� �ٸ� �ٰ����� �˾Ƽ� ������
		
		multiClient.getJta().setCaretPosition(multiClient.getJta().getDocument().getLength());
		
	}
	
	//������ ���� ��� ������Ʈ =========================================
	private void updateUserList(Protocol protocol) {
		List<UserVO> userVoList = (List<UserVO>) protocol.getObject();
		
		List<String> idList = userVoList.stream().map(UserVO::getUserId).toList(); // UserVO���� �̸��� �������� 
		
		multiClient.setJoinUserList(idList);
	}
	
	//ȸ������ �������� ��� ============================================
	private void joinSuccess(Protocol protocol) {
		multiClient.successJoin();
	}
	//�α��� �������� ��� ==============================================
	private void loginSuccess(Protocol protocol) {
		multiClient.successLogin();
	}
	
	
	

}
