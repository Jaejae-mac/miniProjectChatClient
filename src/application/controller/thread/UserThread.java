package application.controller.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;

import application.log.Log;
import application.protocol.Protocol;
import application.protocol.vo.ObjectVO;



public class UserThread extends Thread{
private ObjectInputStream in = null;
	
	public UserThread(ObjectInputStream in) {
		this.in = in;
		Log.sbLog.append("유저의 메세지 수신용 스레드가 생성되었습니다. - " + new Date()+"\n");
	}

	
	
	public void run() {
		ObjectVO vo  = null;
		Log.sbLog.append("유저의 메세지 수신용 스레드가 동작을 시작합니다. - " + new Date()+"\n");
		try {
			while (true) {
				
				vo = (ObjectVO) in.readObject();
				switch (vo.getProtocol()) {
				case Protocol.SEND_MESSAGE:
					Log.sbLog.append("서버로부터 메세지를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setGetMessageVo(vo);
//					System.out.println("[" + vo.getSender() + "] : " + vo.getMessage());
					break;
				case Protocol.SEND_MESSAGE_ROOM:
					GetDataFromUserThread.setGetMessageVo(vo);
					break;
				case Protocol.ENTER_ROOM:
					Log.sbLog.append("서버로부터 메세지(방을 열었습니다.)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setGetMessageVo(vo);
					break;
				case Protocol.CREATE_ROOM:
					Log.sbLog.append("서버로부터 메세지(방을 만들어라.)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setGetMessageVo(vo);
					break;
				case Protocol.ENTER_ROOM_ALLOW:
					Log.sbLog.append("서버로부터 메세지(방 입장 승인)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setGetMessageVo(vo);
					break;
				case Protocol.EXIT_ROOM_DONE:
					Log.sbLog.append("서버로부터 메세지(방 나가기)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setGetMessageVo(vo);
					break;
				case Protocol.CLEAR_CHAT_TEXT:
					Log.sbLog.append("서버로부터 메세지(채팅창을 비워라)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setGetMessageVo(vo);
					
				case Protocol.ID_EXIST:
//					System.err.println("[GET USER INF2222O]"+vo.toString());
					GetDataFromUserThread.setGetComparePwVo(vo);
					break;
					
				case Protocol.INVITE_USER:
					GetDataFromUserThread.setGetComparePwVo(vo);
					break;
				case Protocol.CREATE_GROUP_ROOM:
					break;
					
				case Protocol.UPDATE_CHAT_TITLE:
					GetDataFromUserThread.setGetMessageVo(vo);
					break;
				case Protocol.USER_EXIST:
					Log.sbLog.append("서버로부터 메세지(유저가 존재한다.)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setAddFriendVo(vo);
					
					break;
				case Protocol.CANNOT_FIND_USER:
					Log.sbLog.append("서버로부터 메세지(유저를 찾을 수 없다.)를 받아왔습니다. - " + new Date()+"\n");
//					System.out.println("[CANNOT_FIND_USER]"+vo.toString());
					GetDataFromUserThread.setAddFriendVo(vo);
					break;
				case Protocol.DELETE_DONE:
					Log.sbLog.append("서버로부터 메세지(삭제완료.)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setGetMessageVo(vo);
					break;
				case Protocol.SEND_FILE_REQUEST:
					Log.sbLog.append("서버로부터 메세지(파일을 보내고 싶다.)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setFileCheckVo(vo);
				case Protocol.SEND_FILE_RESPONSE_OK:
					Log.sbLog.append("서버로부터 메세지(파일을 받겠다.)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setFileCheckVo(vo);
					
				case Protocol.SEND_FILE_SUCCESS:
					Log.sbLog.append("서버로부터 메세지(파일 전송 성공.)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setFileCheckVo(vo);
					break;
				case Protocol.SEND_FILE_FAIL:
					Log.sbLog.append("서버로부터 메세지(파일전송 실패.)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setFileCheckVo(vo);
					break;
				case Protocol.LOGOUT:
					Log.sbLog.append("서버로부터 메세지(로그아웃해라.)를 받아왔습니다. - " + new Date()+"\n");
					GetDataFromUserThread.setGetMessageVo(vo);
					break;
				default:
					break;
				}

			}
		} catch (ClassNotFoundException ce) {
//			System.err.println("[ UserThread ] 클라이언트 스레드에서 클래스못찾는 에러 발생.");
			Log.sbLog.append("[ UserThread ] 클라이언트 스레드에서 클래스못찾는 에러 발생. - " + new Date()+"\n");
		} catch (IOException e) {
//			System.err.println("[ UserThread ] 메세지 수신중 에러발생 - 클라이언트 스레드.");
			Log.sbLog.append("[ UserThread ]  메세지 수신중 에러발생 - 클라이언트 스레드. - " + new Date()+"\n");
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
