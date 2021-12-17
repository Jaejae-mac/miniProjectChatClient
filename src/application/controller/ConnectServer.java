package application.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import application.controller.thread.UserThread;
import application.jdbc.repository.vo.UserTableVO;
import application.log.Log;
import application.protocol.Protocol;
import application.protocol.vo.ObjectVO;

public class ConnectServer {
	private Socket socket = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	private String ipAddr = "localhost";
//	private String ipAddr = "192.168.0.18";
	private int port = 7777;
	private ObjectVO vo = null;
	private UserThread userThread;
	private boolean loginFlag;

	public ConnectServer() {
		try {
			socket = new Socket(ipAddr, port);

			Log.sbLog.append("\n*** Connect Server ****\n");
			Log.sbLog.append("   서버와 연결되었습니다.\n");

			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());

			System.out.println(Log.showLog());

		} catch (UnknownHostException e) {
			Log.sbLog.append("호스트를 찾을 수 없습니다. - ConnectServer()\n");
			e.printStackTrace();
		} catch (IOException e) {
			Log.sbLog.append("서버 소켓 생성중 오류가 발생했습니다. - ConnectServer()\n");
			e.printStackTrace();
		}
	}

	// 아이디 존재여부 체크를 위해 서버와 연결할 생성자.
	public ConnectServer(String protocol, String userId) {
		this();
		ObjectVO tvo = new ObjectVO(protocol, userId);
		try {
			oos.writeObject(tvo);
			oos.flush();
			Log.sbLog.append("아이디 존재여부 확인을 위해 서버로 객체전송을 성공 했습니다. - ConnectServer(String,String)\n");
			System.out.println(Log.showLog());
			vo = (ObjectVO) ois.readObject();

		} catch (ClassNotFoundException e) {
			Log.sbLog.append("서버로부터 파일을 읽어오는데 실패했습니다. - - ConnectServer(String,String)\n");
			System.err.println("here");
			e.printStackTrace();
		} catch (IOException e) {
			Log.sbLog.append("아이디 존재여부 확인을 위해 서버로 객체전송을 실패 했습니다. - ConnectServer(String,String)\n");
			e.printStackTrace();
		} finally {

			close(ois, oos);
		}
	}

	public ConnectServer(String protocol, UserTableVO vo) {
		this();
		ObjectVO tvo = new ObjectVO(protocol, vo);
		try {
			oos.writeObject(tvo);
			oos.flush();
			System.out.println("[ 회원가입관련 ]정상적으로 객체를 전달 했습니다.");
		} catch (IOException e) {
			System.err.println("객체 송신중에 에러 발생. - ConnectServer");
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 로그인.
	public ConnectServer(String protocol, ObjectVO vo) {
		this();
		ObjectVO tvo = vo;
		tvo.setProtocol(protocol);
		try {
			// 로그인용 객체 전달.
			oos.writeObject(tvo);
			oos.flush();
			System.out.println("[로그인 시도] 정상적으로 객체를 전달 했습니다.");

			tvo = (ObjectVO) ois.readObject();
			this.vo = tvo;
			System.out.println(this.vo.toString());
			if (tvo.getProtocol().equals(Protocol.LOGIN_SUCCSS)) {
				this.loginFlag = true;
				// 로그인 성공후 스레드 동작 시작.
				userThread = new UserThread(ois);
				userThread.setDaemon(true);
				userThread.start();
			} else {
				this.loginFlag = false;
			}

		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();

		} catch (IOException e) {
			System.err.println("[로그인 시도] 객체 송신중에 에러발생. - ConnectServer");
			e.printStackTrace();
		}
	}

	public ConnectServer(String ip, int port, ObjectVO vo) {
		try {
		
			File t = vo.getFile();
			if (!t.exists()) {
				System.err.println("File is not exist!!!");
			}
			Log.sbLog.append("\n***     Connect FILE Server     ****\n");
			Log.sbLog.append("파일전송을위한 수신자와 연결되었습니다.\n");

//			oos = new ObjectOutputStream(socket.getOutputStream());
//			ois = new ObjectInputStream(socket.getInputStream());

			FileInputStream fis = new FileInputStream(t);
//			socket = new Socket(ip, port);
			System.out.println(port + "kore portnumber desu!!!!!");
			socket = new Socket("localhost", port);
			
			OutputStream os = socket.getOutputStream();
			int readBytes;
			long totalReadBytes = 0;
			long fileSize = t.length();
			byte[] buffer = new byte[10000];
			while ((readBytes = fis.read(buffer)) > 0) {
				os.write(buffer, 0, readBytes);
				totalReadBytes += readBytes;
				System.out.println("In progress: " + totalReadBytes + "/" + fileSize + " Byte(s) ("
						+ (totalReadBytes * 100 / fileSize) + " %)");

			}

//			oos.writeObject(vo);
//			oos.flush();
			System.out.println(Log.showLog());
			fis.close();
			os.close();
			socket.close();
			

		} catch (UnknownHostException e) {
			Log.sbLog.append("파일 수신자 호스트를 찾을 수 없습니다. - ConnectServer()\n");
			e.printStackTrace();
		} catch (IOException e) {
			Log.sbLog.append("서버 소켓 생성중 오류가 발생했습니다. - ConnectServer()\n");
			e.printStackTrace();
		}
	}

	public void sendMsg(ObjectVO vo) {
//		pvo.setProtocol(vo.getProtocol());
//		pvo.setMessage(vo.getMessage());
		// 로그인시 남아있던 pvo 객체에 관한 정보가 set메소드를 통해 갱신되지 않고 로그인시 정보 그대로 가지고
		// 서버로 전송되어 프로토콜번호와 메세지가 갱신되었음에도 불구하고 이전 객체형태로 전송되는 문제가 발생.
		// 하여, 로그인이 끝나면 객체를 null상태로 변경하고, 객체의 정보를 새로 갱신해 주어 해결.
		this.vo = vo;
		System.out.println(this.vo.toString() + "******** ** ******");
		try {
			oos.writeObject(this.vo);
			oos.flush();
			System.out.println("Flush success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Flush Fail");
		}
	}

//	public void getMsg() {
//		ObjectVO tvo = null;
//		try {
//			tvo = (ObjectVO) ois.readObject();
//
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public void close(ObjectInputStream oisC, ObjectOutputStream oosC) {
		try {
			if (oisC != null) {
				oisC.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (oosC != null) {
				oisC.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ObjectVO getVo() {
		return vo;
	}

	public void setVo(ObjectVO vo) {
		this.vo = vo;
	}

	public boolean isLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(boolean loginFlag) {
		this.loginFlag = loginFlag;
	}

}
