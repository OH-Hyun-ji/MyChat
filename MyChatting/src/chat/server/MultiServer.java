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
	
	private ArrayList<MultiServerThread> loginServerList = new ArrayList<MultiServerThread>(); //로그인한 사용자 리스트 
	
	private ArrayList<MultiServerThread> serverThreadList = new ArrayList<MultiServerThread>();//로그인안하고 연결만한 스레드리스트 
	
	private ArrayList<UserVO> joinUserList =new ArrayList<UserVO>(); //접속한 유저 리스트
	
	private UserDB userDb; //db는 서버가 관리
	
	
	//서버 화면구성=================================================================================
	private JFrame serverMain = new JFrame("Server");
	private Label serverLog = new Label("Server Log.....");
	private Label clientUserList = new Label("접속자 목록");
	
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
		serverLogMemo.add("서버가 시작되었습니다.\n");
		System.out.println("서버가 시작 되었습니다. ");
		
		
		boolean isStop = false;
		while(!isStop) {
			System.out.println("서버가 준비 되었습니다");
			serverLogMemo.add("서버가 준비 되었습니다");
			System.out.println("============================");
			try {
				socket = serverSocket.accept();
				System.out.println("서버 대기중~~~~~~!!!!!!!!!");
				serverLogMemo.add("서버 대기중~~!!!!");
				String ipAddr = socket.getInetAddress().getHostAddress();//ip주소 받고 
				
				System.out.println("ip 주소 : "+ ipAddr +"이 접속하였습니다." ); //보여주기
				serverLogMemo.add("ip 주소 : "+ ipAddr +"이 접속하였습니다.");
				System.out.println("============================");
				MultiServerThread serverThread = new MultiServerThread(this);//서버스레드 만들고 
				serverThreadList.add(serverThread);
				Thread t =  new Thread(serverThread); //스레드 생성 
				t.start();
				serverLogMemo.add("현재 연결된 스레드 갯수 : "+serverThreadList.size());
				
				
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			
			
		}
		
	}
	
	
	
	
	public void setEvent() { // 이벤트 버튼
		serverStartBt.addActionListener(this);
		serverExitBt.addActionListener(this);
		
	}
	
	
	public void GUI() {  //서버 화면구성  
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
			JOptionPane.showMessageDialog(null,"서버를 종료합니다","SERVER EXIT", JOptionPane.CLOSED_OPTION);
			System.exit(0);
		}
	
	}
	
	//접근자=======================================
	
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
