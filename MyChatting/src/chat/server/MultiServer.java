package chat.server;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import chat.db.UserDB;
import chat.vo.UserVO;

public class MultiServer extends JFrame implements ActionListener{
	
	private Socket socket;
	
	private ArrayList<MultiServerThread> loginServerList = new ArrayList<MultiServerThread>(); //�α����� ����� ����Ʈ 
	
	private ArrayList<MultiServerThread> serverThreadList = new ArrayList<MultiServerThread>();//�α��ξ��ϰ� ���Ḹ�� �����帮��Ʈ 
	
	private ArrayList<UserVO> joinUserList =new ArrayList<UserVO>(); //������ ���� ����Ʈ
	
	private UserDB userDb; //db�� ������ ����
	
	
	//���� ȭ�鱸��=================================================================================
	private JFrame serverMain = new JFrame("Server");
	private Label serverLog = new Label("Server Log.....");
	private Label clientUserList = new Label("������ ���");
	
	private List serverLogMemo = new List();
	private java.awt.List serverUserList = new java.awt.List();
	
	
	private JButton serverStartBt = new JButton("ServerStart");
	private JButton serverExitBt = new JButton("ServerExit");
	ServerSocket serverSocket;
	
	public MultiServer() throws IOException {
		GUI();
		setEvent();
		startServer();
	}
	public void startServer() {
		try {
			serverSocket = new ServerSocket(5050);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		serverLogMemo.add("������ ���۵Ǿ����ϴ�.\n");
		System.out.println("������ ���� �Ǿ����ϴ�. ");
		
		
		boolean isStop = false;
		while(!isStop) {
			System.out.println("������ �غ� �Ǿ����ϴ�");
			serverLogMemo.add("������ �غ� �Ǿ����ϴ�");
			System.out.println("============================");
			try {
				socket = serverSocket.accept();
				System.out.println("���� �����~~~~~~!!!!!!!!!");
				serverLogMemo.add("���� �����~~!!!!");
				String ipAddr = socket.getInetAddress().getHostAddress();//ip�ּ� �ް� 
				
				System.out.println("ip �ּ� : "+ ipAddr +"�� �����Ͽ����ϴ�." ); //�����ֱ�
				serverLogMemo.add("ip �ּ� : "+ ipAddr +"�� �����Ͽ����ϴ�.");
				System.out.println("============================");
				MultiServerThread serverThread = new MultiServerThread(this);//���������� ����� 
				serverThreadList.add(serverThread);
				Thread t =  new Thread(serverThread); //������ ���� 
				t.start();
				serverLogMemo.add("���� ����� ������ ���� : "+serverThreadList.size());
				
				
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			
			
		}
		
	}
	
	
	
	
	public void setEvent() { // �̺�Ʈ ��ư
		serverStartBt.addActionListener(this);
		serverExitBt.addActionListener(this);
		
	}
	
	
	public void GUI() {  //���� ȭ�鱸��  
		serverMain = new JFrame("server");
		
		serverMain.setDefaultCloseOperation(EXIT_ON_CLOSE);
		serverMain.add("West",new Label());
		serverMain.add("East",new Label());
		serverMain.add("South", new Label());
		serverMain.add("North", new Label());
		
		
		Panel mainPanel = new Panel(new BorderLayout(3,3));
		
		Panel leftPanel = new Panel(new BorderLayout(3,3));
			leftPanel.add(serverLog,"North");
			leftPanel.add(serverLogMemo,"Center");
			leftPanel.setSize(500,300);
			//serverLogMemo.setEditable(false);
			
		Panel rightPanel = new Panel(new BorderLayout(3,3));
			rightPanel.add(clientUserList,"North");
			rightPanel.add(serverUserList,"Center");
			
		Panel bottom = new Panel(new GridLayout(2,1,3,3));
			bottom.add(serverStartBt);
			bottom.add(serverExitBt);
			
		Panel rightPanel1 = new Panel(new BorderLayout(10,10));
			rightPanel1.add(rightPanel,"Center");
			rightPanel1.add(bottom,"South");
			
		
		
		mainPanel.add(leftPanel,"Center");
		mainPanel.add(rightPanel1,"East");
				
		serverMain.add(mainPanel);
		
		serverMain.setSize(600,700);
		serverMain.setResizable(false);
		serverMain.setVisible(true);
		//serverMain.pack();
		
		}
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == serverStartBt) {
		//	startServer();
		}if(e.getSource() == serverExitBt) {
			JOptionPane.showMessageDialog(null,"������ �����մϴ�","SERVER EXIT", JOptionPane.CLOSED_OPTION);
			System.exit(0);
		}
	
	}
	
	//������=======================================
	
	public Socket getSocket() {
		return socket;
	}
	
	public List getServerMemo() {
		return serverLogMemo;
	}
	
	public ArrayList<MultiServerThread> getLoginServerList(){
		return loginServerList;
		
	}
	
	public ArrayList<MultiServerThread> getServerThreadList(){
		return serverThreadList;
	}
	
	public ArrayList<UserVO> getJoinUserList() {
		return joinUserList;
	}
	
	public List getServerUserList() {
		return serverUserList;
	}
	
	public void setServerUserList(java.util.List<String> list) throws EOFException {
		serverUserList.removeAll();
		for(String id : list ) {
			serverUserList.add(id);
			
		
			
		}
		
		
	
		
		
		
	}
	
	
	public static void main(String[] args) throws IOException {
		new MultiServer();
	}
	
}
