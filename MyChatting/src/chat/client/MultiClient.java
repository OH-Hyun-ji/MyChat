package chat.client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import chat.protocol.Action;
import chat.protocol.MyException;
import chat.protocol.Protocol;
import chat.vo.ChatVO;
import chat.vo.FileVO;
import chat.vo.JoinVO;
import chat.vo.LoginVO;
import chat.vo.UserVO;




public class MultiClient extends JFrame implements ActionListener {

	private Socket socket; //������ ����� ����
    private ObjectInputStream ois =null;
    private ObjectOutputStream oos =null;


    
    //============ä��â ������� =========================
    //��ư �̹���=========================================
    ImageIcon img = new ImageIcon("./Button_Image/1.png");
    Image img1 =img.getImage();
    Image changImg = img1.getScaledInstance(30,30,Image.SCALE_SMOOTH);
    ImageIcon changeIcon = new ImageIcon(changImg);
    //=====================================================
 
    Image outImg = new ImageIcon("./Button_Image/i3.jpg").getImage();
 //   Image changeOutImg = outImg.getScaledInstance(30,50,Image.SCALE_SMOOTH);
 //   ImageIcon changeOutIcon = new ImageIcon(changeOutImg);
    
    private JTextArea outChat;// = new JTextArea(30,50) ;
    private JTextField inChat = new JTextField(10);//�Է��ϴ� ä��â
    
    private JButton chatBt = new JButton(changeIcon);
    private JButton chatExit = new JButton("������");
    private JButton userModify = new JButton("ȸ����������");
    private JButton userDelete = new JButton("ȸ��Ż��");
    private JButton fileSend = new JButton("��������");
  
    private Label myInfo = new Label();
    private Label userList = new Label("Member List");
    private java.awt.List joinUserList = new java.awt.List();
  //  private java.util.List<UserVO> userData = new ArrayList<UserVO>();
    

    //============�α��� �������======================

    private JTextField id = new JTextField(10);
    private JTextField pw = new JTextField(15);
    private JButton Login =new JButton("Login");
    private JButton showJoinBtn =new JButton("Join");
    private JLabel upText = new JLabel("Welcome");
    private JLabel upText1 = new JLabel("Simple Is Best Chat~");

    //===========ȸ������ ȭ�鱸���� �ʿ��� ������� =============

    JTextField joinId = new JTextField(10);
    JTextField joinPw = new JTextField(15);
    JTextField checkPw = new JTextField(15);
    JTextField joinName = new JTextField(15);
    JLabel joinTitle = new JLabel("ȸ������");
    JButton joinCheck = new JButton("�ߺ�Ȯ��");
    JButton JoinBtn = new JButton("ȸ������");

  //===========ȸ���������� �������======================
    private JTextField modifyId = new JTextField(10);
    private JTextField modifyPw = new JTextField(15);
    private JTextField modifycheckPw = new JTextField(15);
    private JTextField modifyName = new JTextField(15);
    private JButton userModifyFinish = new JButton("�����Ϸ�");
    private Label ModiTitle = new Label("ȸ������ ����");
    //=============================================

    private JFrame jFrame,loginTool,userJoin,userModi;
    private String userId;
    private JFileChooser fc = new JFileChooser("./");

    
   
    
    

    public MultiClient() {
        super();
        settingClientGUI();//ȭ���� �����ϰ�
        setEvent();//�̺�Ʈ�� �����ϰ�
        this.pack();

    }

    public void init() throws IOException {
        socket = new Socket("127.0.1.1",5050);
        System.out.println("������ ���� ����!!!");

     //====��������??=============
  
        oos= new ObjectOutputStream(socket.getOutputStream());
        ois =new ObjectInputStream(socket.getInputStream());

        MultiClientThread ct = new MultiClientThread(this);
        ct.run();
    }

    public void settingClientGUI() {
        jFrame = new JFrame("Simple Is Best Chat ~");
        jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);//X�������� ���α׷� ���� �߰�
        loginTool= new JFrame("login~");
        loginTool.setDefaultCloseOperation(EXIT_ON_CLOSE);//X�������� ���α׷� ���� �߰�
        userJoin = new JFrame("join~");
        userJoin.setDefaultCloseOperation(EXIT_ON_CLOSE);//X�������� ���α׷� ���� �߰�
        userModi = new JFrame("userModify~");
        userModi.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //=========================ä��â ������ =======================================
       outChat = new JTextArea(30,50) {
    	   {
    		   
    		   setOpaque(false);
    	   }
    	   
        public void paintComponent(Graphics g) {
        	//outImg.getScaledInstance(30,30,ddImage.SCALE_SMOOTH);
        	g.drawImage(outImg,200,150,null);
        
        	super.paintComponent(g);
        }
       };
        
        jFrame.add("West",new Label());
        jFrame.add("East",new Label());
        jFrame.add("South",new Label());
        jFrame.add("North",new Label());

        Panel mainPanel = new Panel(new BorderLayout(3,3));

        Panel leftPanel = new Panel(new BorderLayout());
        
        Panel iPanel = new Panel(new BorderLayout(3,3));
	        iPanel.add("Center",inChat);
	        iPanel.add("East",chatBt);
	        chatBt.setBorderPainted(false); //��ư �ܰ��� ���ֱ�
	        chatBt.setContentAreaFilled(false); //��ư ���� ���� ����
	        chatBt.setFocusPainted(false);//��ư ���ý� �׵θ� ������ 
        Panel BPanel = new Panel(new FlowLayout());
        	BPanel.add(fileSend);
        	BPanel.add(userModify);
        	BPanel.add(userDelete);
        	
        
        leftPanel.add("North",outChat);
        leftPanel.add("South",iPanel);
        
        
        outChat.setEditable(false);//ä��â �Է� ����
        

        Panel rightPanel = new Panel(new BorderLayout(2,2));
        Panel childRightPanel = new Panel(new BorderLayout(2,2));
        rightPanel.add("North",childRightPanel);//�߰�
        childRightPanel.add("South", userList);//�߰�
        childRightPanel.add("North", myInfo);//�߰�

        rightPanel.add("Center",joinUserList);
        rightPanel.add("South",chatExit);


        mainPanel.add("West",leftPanel);
        mainPanel.add("South",BPanel);
        mainPanel.add("East",rightPanel);
        jFrame.add("Center",mainPanel);

        jFrame.pack();
        jFrame.setResizable(false);
        jFrame.setVisible(false);

        File f = new File("./down");//�����ΰ� ��Ŭ���� ������Ʈ ������!!!
        if(!f.exists()) {
        	System.out.println("���� ���� : "+ f.mkdir());
        	
        }
        //========================�α���â ������ =========================================

        loginTool.setLayout(new BorderLayout(50,50));
        loginTool.add("North", new Label());
        loginTool.add("South",new Label());
        loginTool.add("West", new Label());
        loginTool.add("East", new Label());


        Panel mainPanel1 = new Panel(new GridLayout(3,1));


        Panel textPanel = new Panel(new GridLayout(2,1));
        textPanel.add(upText);
        upText.setFont(new Font("Sans-Serif",Font.BOLD,20));
        textPanel.add(upText1);
        upText1.setFont(new Font("Sans-Serif",Font.BOLD,15));

        Panel cPanel = new Panel(new BorderLayout(10,100));

        Panel centPanel1 = new Panel (new GridLayout(2,1,5,5));
        centPanel1.add(new Label("ID : "));
        centPanel1.add(new Label("PW : "));

        cPanel.add("West",centPanel1);

        Panel centPanel = new Panel(new GridLayout(2,1,5,5));
        centPanel.add(id);
        centPanel.add(pw);

        cPanel.add("Center",centPanel);
        Panel btPanel = new Panel(new FlowLayout());
        btPanel.add(Login);
        btPanel.add(showJoinBtn);
        mainPanel1.add("North",textPanel);
        mainPanel1.add("Center",cPanel);
        mainPanel1.add(btPanel);

        loginTool.add(mainPanel1);



        loginTool.pack();
        loginTool.setResizable(false);
        loginTool.setVisible(true);

        //================ȸ������ â===========================================

        userJoin.setLayout(new BorderLayout(20,20));
        userJoin.add("North",new Label());
        userJoin.add("South",new Label());
        userJoin.add("West",new Label());
        userJoin.add("East",new Label());

        Panel mainPanel2 = new Panel(new GridLayout(3,1,7,7));

        Panel centPanel2 = new Panel(new BorderLayout(3,3));
        Panel cPanel2 = new Panel(new GridLayout(4,1,3,3));
        cPanel2.add(new Label("ID: "));
        cPanel2.add(new Label("PW: "));
        cPanel2.add(new Label("Check PW: "));
        cPanel2.add(new Label("NAME: "));

        centPanel2.add("West",cPanel2);


        Panel ccPanel2 = new Panel(new GridLayout(4,1,3,3));
        ccPanel2.add(joinId);
        ccPanel2.add(joinPw);
        ccPanel2.add(checkPw);
        ccPanel2.add(joinName);

        centPanel2.add("Center",ccPanel2);

        Panel cccPanel = new Panel();
        cccPanel.add("North", joinCheck);

        centPanel2.add("East", cccPanel);


        Panel bottom = new Panel(new BorderLayout(30,30));
        bottom.add(JoinBtn);

        mainPanel2.add("Center",joinTitle);
        mainPanel2.add(centPanel2);
        mainPanel2.add(bottom);

        userJoin.add("Center",mainPanel2);

        userJoin.pack();
        userJoin.setVisible(false);
        userJoin.setResizable(false);


    
    //======ȸ������ ����==========================================
    
    userModi.setLayout(new BorderLayout(20,20));
    userModi.add("North",new Label());
    userModi.add("South",new Label());
    userModi.add("West",new Label());
    userModi.add("East",new Label());

    Panel mainPanel3 = new Panel(new GridLayout(3,1,7,7));

    Panel centPanel3 = new Panel(new BorderLayout(3,3));
    Panel cPanel3 = new Panel(new GridLayout(4,1,3,3));
    cPanel3.add(new Label("ID: "));
    cPanel3.add(new Label("PW: "));
    cPanel3.add(new Label("Check PW: "));
    cPanel3.add(new Label("NAME: "));

    centPanel3.add("West",cPanel3);


    Panel ccPanel3 = new Panel(new GridLayout(4,1,3,3));
    ccPanel3.add(modifyId);
    ccPanel3.add(modifyPw);
    ccPanel3.add(modifycheckPw);
    ccPanel3.add(modifyName);
    
    modifyId.setEditable(false);  //����â�� �ִ� ���̵��ʵ� �ۼ� ���� !
    
    
    centPanel3.add("Center",ccPanel3);

    Panel cccPanel1 = new Panel();
    cccPanel1.add("North",new Label());

    centPanel3.add("East", cccPanel1);


    Panel bottom1 = new Panel(new BorderLayout(30,30));
    bottom1.add(userModifyFinish);

    mainPanel3.add("Center",ModiTitle);
    mainPanel3.add(centPanel3);
    mainPanel3.add(bottom1);

    userModi.add("Center",mainPanel3);

    userModi.pack();
    userModi.setVisible(false);
    userModi.setResizable(false);
    }


    public void setEvent() {
    	inChat.addActionListener(this);
        showJoinBtn.addActionListener(this);
        JoinBtn.addActionListener(this);
        Login.addActionListener(this);
        chatExit.addActionListener(this);
        chatBt.addActionListener(this);
        joinCheck.addActionListener(this);
        joinUserList.addActionListener(this);
        userModify.addActionListener(this);
        userDelete.addActionListener(this);
        userModifyFinish.addActionListener(this);
        fileSend.addActionListener(this);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource() == showJoinBtn) {
			loginTool.setVisible(false);
			userJoin.setVisible(true);
		}
		if(e.getSource() == JoinBtn) {
				join();
		}
		if(e.getSource() == Login) {
			login();
		}
		if(e.getSource() == chatExit) {
			userExit();//
			System.exit(0);
		}
		if(e.getSource() == joinCheck) { //���̵� �ߺ�Ȯ��
			joinCheck();
		}
		if(e.getSource() == chatBt || e.getSource() == inChat) {
			sendChat();
		}if(e.getSource() == joinUserList) {
			whisper();//�����ڸ�� �������� �׳��� �ӼӸ� ...
		}
		if(e.getSource()== userModify) {  //ȸ������ ������������
			userModify();  
		}if(e.getSource()== userDelete) { //ȸ������ �����Ҷ� 
			userDelete();
		}if(e.getSource()== userModifyFinish) {//ȸ������������ �����Ϸ��ư �������� 
			userModifyFinsh();
		}
		if(e.getSource() == fileSend) {
			fileSendTo();
		}
	}
		
		catch (IOException e1) {	
		e1.printStackTrace();
	}
		
	}
//���� ���ۿ�	




	private void fileSendTo() throws IOException {
		FileVO file = new FileVO();
		fc.setDialogTitle("������ ������ �����ϼ���!!!");
		fc.showOpenDialog(this);
		if(fc.getSelectedFile() == null || fc.getSelectedFile().getName().length() == 0) {
			JOptionPane.showMessageDialog(null, "������ ������ �����ϼ���","File Send Choose",JOptionPane.WARNING_MESSAGE);
			return;
		}
		//JOptionPane.showInternalMessageDialog(null,"���������� �Ϸ�Ǿ����ϴ�." ,"File Send Success",JOptionPane.OK_OPTION); // ����.. ������פ�����
		
		
		
		oos.writeObject(new Protocol(Action.FILE_SEND_REQUSET,fileSend));
		
	}
	

	private void userDelete() throws IOException {
		jFrame.setVisible(false);
		loginTool.setVisible(true);
		oos.writeObject(new Protocol(Action.DELETE,userId));//?�ϴµ�
		
	}

	private void userModifyFinsh() throws IOException { //ȸ������ ������ư�������� 
		userModi.setVisible(false);
		jFrame.setVisible(true);
		oos.writeObject(new Protocol(Action.MODIFY,makeUserModi()));
		
	}

	private void userModify() throws IOException  {// ȸ������������������ 
		userModi.setVisible(true);
		
		jFrame.setVisible(false);
		oos.writeObject(new Protocol(Action.MODIFY_BASIC,userId));
		
		
		
		
	}
	
	private UserVO makeUserModi() {
		UserVO vo = new UserVO();
		vo.setUserId(userId);
		vo.setUserPw(modifyPw.getText());
		vo.setUserName(modifyName.getText());
		return vo;
		
		
	}

	private void userExit() throws IOException {
		ChatVO chatVo = new ChatVO();
		chatVo.setWriterId(userId);
		chatVo.setMessage(inChat.getText());
		oos.writeObject(new Protocol(Action.EXIT,chatVo));
		System.exit(ABORT);
		
	}

	//�޼��� ����!!=======================================================
	private void sendChat() throws IOException {
		ChatVO chatVo = new ChatVO();
		chatVo.setWriterId(userId);
		chatVo.setMessage(inChat.getText());
		String[] list = inChat.getText().split(" "); //"# 1 #���� �ӼӸ� zz => #,1,#����,�ӼӸ�,zz
		
		if(list[0].startsWith("#") && list.length >0) {
			String whisperId = list[1];
			
			if(!whisperId.equals(userId)) {
				chatVo.setWhisperId(whisperId);
				
				String[] split = inChat.getText().split(":");
				chatVo.setMessage(split[1]);
				
			}else if(whisperId.equals(userId)){ // �����Ѱ� �� �ڽ��̶��
				chatVo.setWhisperId(userId);
				String[] split = inChat.getText().split(":");
				chatVo.setMessage(split[1]);
			}
		}
		oos.writeObject(new Protocol(Action.CHAT,chatVo));
		
		
		inChat.setText("");
		
	}
	//�ӼӸ�============================================================
	private void whisper() throws IOException {
		String id = joinUserList.getSelectedItem();
		inChat.setText("# "+id+" #���� �ӼӸ�:");
		
		
	
	}
	
	
	
	
	
	
	
	
	//���̵� �ߺ� üũ=======================================================
	private void joinCheck() throws IOException {
		oos.writeObject(new Protocol(Action.ID_CHECK,joinId.getText()));
	}
	
	
	//ȸ������===============================================================
	private void join() throws IOException {
		if(joinId.getText().length() !=0 && joinPw.getText().length() != 0 && checkPw.getText().length()!= 0 && joinName.getText().length()!= 0 ) {
			if(!joinPw.getText().equals(checkPw.getText()) ) {
				JOptionPane.showMessageDialog(null,"��й�ȣ ����ġ!!!","join_null",JOptionPane.WARNING_MESSAGE);
				return;
			}
			oos.writeObject(new Protocol(Action.JOIN, makeJoinVo()));
			
			return;
		}
		else if(joinId.getText().length() ==0 || joinPw.getText().length() ==0|| checkPw.getText().length()==0||joinName.getText().length() ==0) {
			JOptionPane.showMessageDialog(null,"��ĭ���� �ۼ��� �ּ���","join_null",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		return;
		
	}
	
	private JoinVO makeJoinVo() {
		JoinVO joinVo = new JoinVO();
		joinVo.setUserId(joinId.getText());
		joinVo.setUserPw(joinPw.getText());
		joinVo.setUserPwCk(checkPw.getText());
		joinVo.setUserName(joinName.getText());
		return joinVo;
		
	}
	//ȸ������ ������!!
	public void successJoin() {
		JOptionPane.showMessageDialog(null, "ȸ������ ����!!","Join_Success",JOptionPane.OK_OPTION);
		userJoin.setVisible(false);
		loginTool.setVisible(true);
	}
	//�α��� ================================================================
	
	private void login() throws IOException {
		oos.writeObject(new Protocol(Action.LOGIN,makeLoginVo()));
	}
	
	//�α������� ���� ������
	private LoginVO makeLoginVo() {
		LoginVO loginVo = new LoginVO();
		loginVo.setUserId(id.getText());
		loginVo.setUserPw(pw.getText());
		
		return loginVo;
	}
	//�α��� ������
	public void successLogin() {
		userId = id.getText();
		jFrame.setVisible(true);
		loginTool.setVisible(false);
		myInfo.setText("MyId : ["+ userId+"]");
	}
	
		
	public void setJoinUserList(List<String> idList) {//���Ӷ����� ����Ʈ ������Ʈ
		
		joinUserList.removeAll(); //���縮��Ʈ ���� ����!
		
		for(String test_id : idList)
		{
			System.out.println("Test Test:: "+ test_id);
		}
		
		idList.forEach(id -> joinUserList.add(id));
		
		
		
	}
	//////////////////////////////////////////////////////////////////
	public void setUserList(List<String> idList) {

		if(joinUserList.getItemCount() > 0){
			joinUserList.removeAll();
		}
//		joinUserList.removeAll();//���縮��Ʈ ���� ����!
		//idList.forEach(id -> joinUserList.add(id));
		for(String id : idList){
		   joinUserList.add(id);
		}
		
	}
	
	
	
	public static void main(String[] args) throws Exception {
		MultiClient multiClient = new MultiClient();
		multiClient.init();    
	}
	
	public JTextField getModiId () {
		return modifyId;
	}
	public JTextField getModiPw() {
		return modifyPw;
		
	}
	
	public JTextField getModiCheckPw() {
		return modifycheckPw;
	}
	
	public JTextField getModiName() {
		return modifyName;
	}
	
	public java.awt.List getJoinUserList() {
		return joinUserList;
	}
	
	public ObjectInputStream getOis() {
		return ois;
	}
		
	public JTextArea getJta() {
		return outChat;
	}
	
	public void idCheckSuccess() {
		JOptionPane.showMessageDialog(null,"���̵� ��밡��","Id_OK",JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void idCheckFail() {
		JOptionPane.showMessageDialog(null,"�̹� ������� ���̵� �Դϴ�.","Id_Fail",JOptionPane.WARNING_MESSAGE);
	}

	public void idCheckFailZero() {
		JOptionPane.showMessageDialog(null,"���̵� �Է����ּ���!!!","Id_Fail!",JOptionPane.WARNING_MESSAGE);
		
	}

	public void joinFail() {
		JOptionPane.showMessageDialog(null,"��й�ȣ�� ��ġ�����ʽ��ϴ�.","password_Fail",JOptionPane.WARNING_MESSAGE);
		
	}

}
