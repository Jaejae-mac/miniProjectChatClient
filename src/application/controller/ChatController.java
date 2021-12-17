package application.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import javax.xml.ws.handler.MessageContext.Scope;

import application.controller.file.FileController;
import application.controller.thread.GetDataFromUserThread;
import application.jdbc.repository.vo.UserTableVO;
import application.log.Log;
import application.protocol.Protocol;
import application.protocol.vo.ObjectVO;
import application.protocol.vo.Room;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

//메인 화면을 관리할 컨트롤러
public class ChatController implements Initializable {

	@FXML
	private VBox fx_chatVbox;
	@FXML
	private MenuBar menubar;
	@FXML
	private MenuItem fx_close;
	@FXML
	private MenuItem fx_about;
	@FXML
	private TabPane fx_tabPane;
	@FXML
	private Tab fx_myInfoTab;
	@FXML
	private Text fx_myIdT;
	@FXML
	private Text fx_nameT;
	@FXML
	private ComboBox<String> fx_userTel1Cb;
	@FXML
	private TextField fx_userTel2Tf;
	@FXML
	private TextField fx_userTel3Tf;
	@FXML
	private TextField fx_userEmail1Tf;
	@FXML
	private ComboBox<String> fx_userEamil2Cb;
	@FXML
	private Button fx_deleteBtn;
	@FXML
	private Button fx_updateBtn;

	private ObservableList<String> telList = FXCollections.observableArrayList("010", "011", "016", "019", "018");
	private ObservableList<String> dotComList = FXCollections.observableArrayList("naver.com", "hanmail.net",
			"daum.net", "hotmail.com", "gamil.com", "icloud.com", "nate.com");

	@FXML
	private Tab fx_friendsTab;
	@FXML
	private TextField fx_searchTf;
	@FXML
	private Button fx_addFriendBtn;
	@FXML
	private ScrollPane fx_friendScrollPane;
	@FXML
	private ListView<String> fx_friendList;
	private ObservableList<String> fList = FXCollections.observableArrayList();

	@FXML
	private Tab fx_roomTab;
	@FXML
	private ScrollPane fx_roomScrollPane;
	@FXML
	private ListView<String> fx_roomList;
	private ObservableList<String> rList = FXCollections.observableArrayList();
	private Room room = null;
	@FXML
	private Tab fx_chatTab;
	@FXML
	private Text fx_receiverT;
	@FXML
	private ScrollPane fx_chatScrollPane;
	@FXML
	private TextArea fx_chatTa;
	@FXML
	private ToolBar fx_toolbar;
	@FXML
	private Button file_btn;
	@FXML
	private Button show_log;
	@FXML
	private TextField fx_chatTf;
	@FXML
	private Button fx_sendBtn;
	@FXML
	private Button clear_btn;

	@FXML
	private Button invite_user;

	private ConnectServer connectServer = null;
	private ObjectVO vo;

	private boolean stop = false;

	private FileChooser fileChooser;

	private ContextMenu contextMenu;
	private ContextMenu contextMenu2;

	private Stage chatStage;

	private File file;

	private ArrayList<Room> myRoomList;

	private int roomNoMember;

	private String deleteRoomNnumber;

	private ArrayList<String> myFriendList;

	public void setConnectServer(ConnectServer connectServer) {
		this.connectServer = connectServer;
		this.vo = connectServer.getVo();
		fx_myIdT.setText(this.vo.getUserId());
		fx_nameT.setText(this.vo.getuVo().getUserName());
		String tel[] = this.vo.getuVo().getUserTel().split("-");
		String email[] = this.vo.getuVo().getUserEmail().split("@");
		fx_userTel1Cb.setValue(tel[0]);
		fx_userTel2Tf.setText(tel[1]);
		fx_userTel3Tf.setText(tel[2]);

		fx_userEmail1Tf.setText(email[0]);

		fx_userEamil2Cb.setValue(email[1]);

		for (String friendId : this.vo.getLiFriend()) {
			fList.add(friendId);
			myFriendList.add(friendId);
		}
		myRoomList = this.vo.getRoomList();
		setRoomList(this.vo);
		Log.sbLog.append("[ DB 알림 ]" + fList.size() + "명의 친구목록을 불러왔습니다.- " + new Date() + "\n");
		fx_friendList.setItems(fList);

	}

	public void setRoomList(ObjectVO tempVo) {
		String roomTitle = "";
		int tempNo = 0;
		rList.clear();
		for (Room r : tempVo.getRoomList()) {
			roomTitle = String.valueOf(r.getRoomNo());
			for (String uid : r.getUsers()) {
				roomTitle = roomTitle + ":" + uid;
			}

			rList.add(roomTitle);

			tempNo = r.getRoomNo();
		}
		Log.sbLog.append("[ DB 알림 ]" + fList.size() + "명의 친구목록을 불러왔습니다.- " + new Date() + "\n");

		fx_roomList.setItems(rList);
	}

	public void gotoRoom(ObjectVO tempVo) {
		String title = "";
		for (String t : rList) {
			String t2[] = t.split(":");
			if (t2[1].equals(tempVo.getReceiver())) {
				title = t;
				break;
			}
		}

		fx_receiverT.setText(title);

		fx_roomList.getSelectionModel().select(-1);
		fx_tabPane.getSelectionModel().select(fx_chatTab);

		String[] ts = fx_receiverT.getText().split(":");
		ObjectVO temVo = new ObjectVO(Protocol.ENTER_ROOM, fx_myIdT.getText());
		temVo.setRoomNo(Integer.parseInt(ts[0]));
		temVo.setRoomList(myRoomList);
		temVo.setReceiver(ts[1]);
		fx_chatTa.clear();

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Log.sbLog.append("[ 컨트롤러 알림 ] 메인 화면에 진입했습니다." + new Date() + "\n");
		fx_userTel1Cb.setItems(telList);
		fx_userEamil2Cb.setItems(dotComList);

		fx_chatScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		fx_chatScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		fx_friendScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		fx_friendScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		fx_roomScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		fx_roomScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		
		
		this.fx_chatTf.setStyle(
				"-fx-border-width: 0 2 2 2, 2 0 0 0;\n" +
		        "-fx-border-radius: 6,6;\n" +
		        "-fx-border-color: #FFA7A7, #FFA7A7;\n" +
		        "-fx-background-color: #EAEAEA;"
		);
		this.fx_chatTa.setStyle(
				"-fx-border-width: 0 2 2 2, 2 0 0 0;\n" +
		        "-fx-border-radius: 6,6;\n" +
		        "-fx-border-color: #FFA7A7, #FFA7A7;\n" +
		        "-fx-background-color: #EAEAEA;"
		);
		
		
		myFriendList = new ArrayList<String>();

		contextMenu = new ContextMenu();
		contextMenu2 = new ContextMenu();
		contextMenuView();

		fileChooser = new FileChooser();
		
		
		fx_close.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(AlertController.alertCon(AlertType.CONFIRMATION, "종료", "", "종료하시겠습니까?")) {
					setMsgServer(new ObjectVO(Protocol.LOGOUT,fx_myIdT.getText()));
					Platform.exit();
				}else {
					event.consume();
				}
				
			}
			
		});
		
		fx_about.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				AlertController.alertCon(AlertType.INFORMATION, "버전정보", "Version - Information", "My Talk\nv1.0.0-release1");
			}
		});

		// 사용자가 보낼 메세지를 입력하는 텍스트 필드 처리를 위한 이벤트 핸들러.
		// 텍스트필드에서 엔터키 를 누르고 땔경우,
		// 그리고 텍스트 필드에서 얻어온 문자열의 길이가 0 보다 크다면,
		// 서버로 전송할 객체형태로 생성하여, ConnectServer 객체를통해 서버로 전송한다.
		// 그리고 텍스트필드를 깔끔하게 비운다.
		fx_chatTf.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					sendAction();
				}
			}
		});

		// 친구목록 리스트뷰의 클릭 처리를 할 이벤트 등록.
		// 더블클릭하면 동작하도록 구성.
		// 클릭을하면 선택된 아이템을 객체로 읽어온다.
		// 만약 빈공간을 클릭하여 객체에 null이 반환된다면 메소드를 종료시켜 널포인트를 방지한다.
		// 빈공간이 아니라 아이템을 정확히 클릭했다면, 서버에 전송할 타입의 객체로 생성한다.
		// 클릭한 아이디 : 받는사람
		fx_friendList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					Object obj = fx_friendList.getSelectionModel().getSelectedItem();
					if (obj == null) {
						return;
					}
//					vo = new ObjectVO("", fx_myIdT.getText(), obj.toString(), null);
//					vo.setUserId(fx_myIdT.getText());
//					vo.setReceiver(obj.toString());
//					System.out.println(vo.toString());
//					fx_friendList.getSelectionModel().select(-1);
//					fx_tabPane.getSelectionModel().select(fx_chatTab);

					ObjectVO tempvo = new ObjectVO(Protocol.ENTER_ROOM, fx_myIdT.getText(), obj.toString(), null);
					Room myRRom = null;
					System.err.println("클릭하면!! "+myRoomList);
					for(Room myr : myRoomList) {
						if(myr.getUsers().size() == 1 && myr.getUsers().get(0).equals(obj.toString())) {
							myRRom = myr;
						}
					}
					if(myRRom != null) {
						tempvo.setRoomNo(myRRom.getRoomNo());
					}
					
//					String[] ts = fx_receiverT.getText().split(":");
//					Room tempRoom = new Room();
//					tempRoom.setRoomNo(Integer.parseInt(ts[0]));
//					tempRoom.setUserId(fx_myIdT.getText());
//					ArrayList<String> arrr = new ArrayList<String>();
//					for(int i = 1; i < ts.length; i++) {
//						arrr.add(ts[i]);
//					}
//					tempRoom.setUsers(arrr);
//					tempRoom.setChatText(fx_chatTa.getText());
//					tempvo.setRoom(tempRoom);
					setMsgServer(tempvo);
//					fx_chatScrollPane.setVvalue(1.0);

				}

			}

		});

		// 방에서 더블클릭시.
		fx_roomList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			// 룸 클릭시 룸번호만 떼다가 뿌려주면됨.
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					Object obj = fx_roomList.getSelectionModel().getSelectedItem();
					if (obj == null) {
						return;
					}
					String roomName = obj.toString();
					System.out.println(roomName+"~~~~~~~~~~~~~~");
					fx_receiverT.setText(roomName);

					fx_roomList.getSelectionModel().select(-1);
					fx_tabPane.getSelectionModel().select(fx_chatTab);

					String[] ts = roomName.split(":");
					System.out.println(" ------ ROOM NUMBER : " + ts[0]);

					Room tempRoom = new Room();
					tempRoom.setRoomNo(Integer.parseInt(ts[0]));
					tempRoom.setUserId(fx_myIdT.getText());
					ArrayList<String> arrr = new ArrayList<String>();
					for (int i = 1; i < ts.length; i++) {
						arrr.add(ts[i]);
					}
					tempRoom.setUsers(arrr);
//					tempRoom.setChatText(fx_chatTa.getText());

					ObjectVO temVo = new ObjectVO(Protocol.ENTER_ROOM, fx_myIdT.getText());
					temVo.setRoomNo(Integer.parseInt(ts[0]));
					temVo.setRoomList(myRoomList);
					temVo.setReceiver(ts[1]);
					temVo.setRoom(tempRoom);
					fx_chatTa.clear();
					setMsgServer(temVo);
				}

			}
		});

		// 리스트에서 우클릭시.
		fx_friendList.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

			@Override
			public void handle(ContextMenuEvent event) {
				contextMenu.show(fx_friendList, event.getScreenX(), event.getScreenY());

			}

		});

		fx_roomList.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

			@Override
			public void handle(ContextMenuEvent event) {
				contextMenu2.show(fx_roomList, event.getScreenX(), event.getScreenY());

			}
		});

		Thread msgThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					Platform.runLater(() -> {
						if (GetDataFromUserThread.getGetMessageVo() != null) {
							getMsgServer(GetDataFromUserThread.getGetMessageVo());
						}
						if (GetDataFromUserThread.getFileCheckVo() != null) {
							getMsgServer(GetDataFromUserThread.getFileCheckVo());
						}
					});
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Log.sbLog.append("[ 알림 ] 메세지 감시용 스레드에서 에러 발생. " + new Date() + "\n");
					}
				}
			}
		};
		msgThread.setDaemon(true);
		msgThread.start();
	}

	
	
	
	// 메세지를 서버로 보내는 일을 담당할 메소드.
	public void setMsgServer(ObjectVO vo) {
		if (vo == null) {
			if (fx_chatTf.getText().length() > 0) {
				ObjectVO tvo = new ObjectVO(Protocol.SEND_MESSAGE, fx_myIdT.getText(), fx_receiverT.getText(),
						fx_chatTf.getText());
				tvo.setLiFriend(this.vo.getLiFriend());
				connectServer.sendMsg(tvo);

			}
		} else {
			connectServer.sendMsg(vo);
		}

	}

	public void getMsgServer(ObjectVO vo) {
		GetDataFromUserThread.setGetMessageVo(null);
		GetDataFromUserThread.setFileCheckVo(null);
		if (vo.getProtocol().equals(Protocol.SEND_MESSAGE) || vo.getProtocol().equals(Protocol.ENTER_ROOM)) {
			fx_chatTa.clear();
			String[] tempArr = vo.getMessage().split(":");
			StringBuilder sb = new StringBuilder();
			for (String t : tempArr) {
				sb.append(t + "\n");
			}
			fx_chatTa.setText(sb.toString());

			fx_chatScrollPane.setVvalue(1.0);

			return;
		}
		if (vo.getProtocol().equals(Protocol.ENTER_ROOM_ALLOW)) {
			System.out.println(vo.toString());
			myRoomList = vo.getRoomList();
			
			String title = String.valueOf(vo.getRoomNo());
			for(String t : vo.getRoom().getUsers()) {
				title = title+":"+ t;
			}
//			fx_receiverT.setText(vo.getRoomNo() + ":" + vo.getReceiver());
			fx_receiverT.setText(title);
			this.roomNoMember = vo.getRoomNo();
			fx_friendList.getSelectionModel().select(-1);
			fx_tabPane.getSelectionModel().select(fx_chatTab);

			fx_chatTa.clear();
			String[] tempArr = vo.getMessage().split(":");
			StringBuilder sb = new StringBuilder();
			for (String t : tempArr) {
				sb.append(t + "\n");
			}
//			int caretPosition = fx_chatTa.caretPositionProperty().get();
			fx_chatTa.setText(sb.toString());

		}

		if (vo.getProtocol().equals(Protocol.ROOM_EXIST)) {

			gotoRoom(vo);
			return;
		}

		if (vo.getProtocol().equals(Protocol.SEND_MESSAGE_ROOM)) {
			System.out.println(vo.toString());
			myRoomList = vo.getRoomList();
			Room thisRoom = null;
			for(Room tempR : myRoomList) {
				if(tempR.getRoomNo() == vo.getRoomNo()) {
					thisRoom = tempR;
					break;
				}
			}
			
			
//			String title = String.valueOf(vo.getRoomNo());
//			for(String t : vo.getRoom().getUsers()) {
//				title = title+":"+ t;
//			}
			String title = String.valueOf(vo.getRoomNo());
			for(String t : thisRoom.getUsers()) {
				title = title+":"+ t;
			}
			
			fx_receiverT.setText(title);
			
			ObjectVO setRoomObj = new ObjectVO();
			setRoomObj.setRoomList(myRoomList);
			setRoomList(setRoomObj);
			
			fx_chatTa.clear();
			String[] tempArr = vo.getMessage().split(":");
			StringBuilder sb = new StringBuilder();
			for (String t : tempArr) {
				sb.append(t + "\n");
			}
//			int caretPosition = fx_chatTa.caretPositionProperty().get();
			fx_chatTa.setText(sb.toString());
			return;
		}

		if (vo.getProtocol().equals(Protocol.CREATE_ROOM)) {
			myRoomList = vo.getRoomList();
			setRoomList(vo);
//			vo.setProtocol(Protocol.ENTER_ROOM_ALLOW);
//			GetDataFromUserThread.setGetMessageVo(vo);
		}
		
		//방이 삭제된후 방을 다시 갱신시킴.
		if(vo.getProtocol().equals(Protocol.EXIT_ROOM_DONE)) {
			myRoomList = vo.getRoomList();
			ObjectVO newRooms = new ObjectVO(null,vo.getUserId());
			newRooms.setRoomList(myRoomList);
			setRoomList(newRooms);
			
			
		}
		
		if(vo.getProtocol().equals(Protocol.INVITE_USER)) {
			myRoomList = vo.getRoomList();
			String tempRoomTitle = String.valueOf(vo.getRoomNo());
			int roomNum = vo.getRoomNo();
			Room tempRoom = null;
			for(Room rr : myRoomList) {
				if(rr.getRoomNo() == roomNum) {
					tempRoom = rr;
				}
			}
			for(String tempUser : tempRoom.getUsers()) {
				tempRoomTitle = tempRoomTitle + ":" + tempUser;
			}
			fx_receiverT.setText(tempRoomTitle);
			ObjectVO newRooms = new ObjectVO(null,vo.getUserId());
			newRooms.setRoomList(myRoomList);
			setRoomList(newRooms);
			sendAction();
		}
		
		if(vo.getProtocol().equals(Protocol.UPDATE_CHAT_TITLE)) {
			myRoomList = vo.getRoomList();
			String tempRoomTitle = String.valueOf(vo.getRoomNo());
			int roomNum = vo.getRoomNo();
			Room tempRoom = null;
			for(Room rr : myRoomList) {
				if(rr.getRoomNo() == roomNum) {
					tempRoom = rr;
				}
			}
			for(String tempUser : tempRoom.getUsers()) {
				tempRoomTitle = tempRoomTitle + ":" + tempUser;
			}
			System.err.println(vo.getUserId()+"의 현재 방이름!!! : "+ tempRoomTitle+"\n");
			fx_receiverT.setText(tempRoomTitle);
			ObjectVO newRooms = new ObjectVO(null,vo.getUserId());
			newRooms.setRoomList(myRoomList);
			setRoomList(newRooms);
			sendAction();

			
		}
		
		if(vo.getProtocol().equals(Protocol.DELETE_DONE)) {
			fList.clear();
			myFriendList.clear();
			for(String newLi : vo.getLiFriend()) {
				fList.add(newLi);
				myFriendList.add(newLi);
			}
			
			myFriendList.clear();
			for (String tf : fList) {
				myFriendList.add(tf);
			}
			fx_friendList.setItems(fList);
		}


		if (vo.getProtocol().equals(Protocol.SEND_FILE_REQUEST)) {
			System.err.println(vo.getReceiver() + "  IN");
			boolean rst = AlertController.alertCon(AlertType.CONFIRMATION, "파일 요청", "파일 전송 요청",
					vo.getReceiver() + "님으로 부터 " + vo.getFileName() + " 파일을 받으시겠습니까?");
			ObjectVO tempVo1 = null;
			if (rst) {
				tempVo1 = new ObjectVO(Protocol.SEND_FILE_RESPONSE_OK, fx_myIdT.getText(), vo.getUserId(), null);
				System.out.println("받는이는 : " + vo.getReceiver());
				// 전송요청을 수락했으므로 서버소켓을 열어서 객체를 수신할 준비를 해야한다. 그리고 서버로 전송되는 객체에는 아이피와 포트넘버가 존재해야
				// 한다.
				fileChooser.setTitle("저장하기");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("문자파일 : Text Files", "*.txt"),
						new ExtensionFilter("그림파일 : Image Files", "*.png", "*.jpg", "*.gif"),
						new ExtensionFilter("음악파일 : Audio Files", "*.wav", "*.mp3", "*.aac"),
						new ExtensionFilter("동영상 : Video Files", "*.mp4", "*.mpv", "*.mkv"),
						new ExtensionFilter("그외 사용자지정 : All Files", "*.*"));

				File tFile = fileChooser.showSaveDialog(chatStage);
				if (tFile != null) {
					FileController fc = new FileController();// here
					fc.setPath(tFile.getPath());
					connectServer.sendMsg(tempVo1);
				}

			} else {
				String tarr[] = fx_receiverT.getText().split(":");
				tempVo1 = new ObjectVO(Protocol.SEND_FILE_RESPONSE_NO, fx_myIdT.getText(), tarr[1], null);
				connectServer.sendMsg(tempVo1);
			}

			return;
		}

		// 상대방으로 부터 파일 전송을 해도 좋다는 응답을 받으면 상대 아이피정보와 포트 넘버를 매개로 다이렉트로 연결하여 전송.
		if (vo.getProtocol().contentEquals(Protocol.SEND_FILE_RESPONSE_OK)) {
			System.err.println("in");
			vo.setFile(file);
			// 파일 전송하는 부분.
			System.out.println("chatController - 파일 전송부 ");
			FileInputStream fis;

			try {
				fis = new FileInputStream(file);
				Socket socket2 = new Socket(vo.getIpAddr(), vo.getPort());

				OutputStream os = socket2.getOutputStream();
				int readBytes;
				long totalReadBytes = 0;
				long fileSize = file.length();
				totalReadBytes = 0;
				fileSize = file.length();
				byte[] buffer = new byte[10000];

				while ((readBytes = fis.read(buffer)) > 0) {
					os.write(buffer, 0, readBytes);
					totalReadBytes += readBytes;
					System.out.println("In progress: " + totalReadBytes + "/" + fileSize + " Byte(s) ("
							+ (totalReadBytes * 100 / fileSize) + " %)");
				}
				socket2.close();
				
//				ObjectVO doneVo = new ObjectVO(Protocol.SEND_MESSAGE_ROOM, vo.getReceiver(), vo.getReceiver(),
//						"파일전송을 완료했습니다.");
//				ArrayList<String> li = new ArrayList<String>();
//				for (String t : fList) {
//					li.add(t);
//				}
//				doneVo.setLiFriend(myFriendList);
//				doneVo.setRoomList(myRoomList);
//				doneVo.setRoom(room);
//				doneVo.setLiFriend(li);
//				setMsgServer(doneVo);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

		}

		if (vo.getProtocol().equals(Protocol.SEND_FILE_FAIL)) {
			AlertController.alertCon(AlertType.INFORMATION, "알림", "알림", "상대방이 로그인중이 아니라 파일전송에 실패했습니다.");
			return;
		}
		if (vo.getProtocol().equals(Protocol.CLEAR_CHAT_TEXT)) {
			fx_chatTa.clear();
			String[] tempArr = vo.getMessage().split(":");
			StringBuilder sb = new StringBuilder();
			sb.append(tempArr[0]);
			fx_chatTa.setText(sb.toString());

		}

		if (vo.getProtocol().equals(Protocol.LOGOUT)) {
			AlertController.alertCon(AlertType.INFORMATION, "알림", "알림", "관리자 권한으로 강제 로그아웃 되셨습니다.");
			ObjectVO tempVo = new ObjectVO(Protocol.LOGOUT, fx_myIdT.getText());
			setMsgServer(tempVo);
			Platform.exit();
			System.exit(0);
		}
	}
	
	
	public void sendAction() {

		ObjectVO temVo = new ObjectVO(Protocol.SEND_MESSAGE_ROOM, fx_myIdT.getText());
		String[] ts = fx_receiverT.getText().split(":");
		Room tempRoom = new Room();
		tempRoom.setRoomNo(Integer.parseInt(ts[0]));
		tempRoom.setUserId(fx_myIdT.getText());
		ArrayList<String> arrr = new ArrayList<String>();
		for (int i = 1; i < ts.length; i++) {
			arrr.add(ts[i]);
		}
		tempRoom.setUsers(arrr);
		tempRoom.setChatText(fx_chatTa.getText());
		temVo.setRoom(tempRoom);

		temVo.setRoomNo(Integer.parseInt(ts[0]));
		temVo.setRoomList(myRoomList);
		temVo.setMessage(fx_chatTf.getText());
		temVo.setLiFriend(myFriendList);
		room = tempRoom;
		setMsgServer(temVo);

		fx_chatTf.clear();
		Log.sbLog.append("[ 전송 알림 ] 서버로 메세지를 전송했습니다." + fx_myIdT.getText() + "  ->  "
				+ fx_receiverT.getText() + " - " + new Date() + "\n");

		fx_chatScrollPane.setVvalue(1.0);
	}
	
	@FXML
	public void sendEvent(ActionEvent event) {
		sendAction();
	}
	

	// 유저의 정보를 업데이트
	@FXML
	public void updateUser(ActionEvent event) {
		Log.sbLog.append("[ 업데이트 알림 ]  사용자 정보 업데이트를 시도합니다." + new Date() + "\n");
		UserTableVO uVo = vo.getuVo();

		// 이메일 전화번호란에 공백이 존재한다면 입력해야 업데이트 할 수 있따고 얼럿하기

		if (fx_userTel2Tf.getText().length() < 4 || fx_userTel3Tf.getText().length() < 4
				|| fx_userEmail1Tf.getText().length() == 0) {
			AlertController.alertCon(AlertType.INFORMATION, "알림", "경고", "전화번호 또는 이메일을 정확하게 입력해주세요.");

		} else {
			String tel = "";
			if (fx_userTel1Cb.getValue() == null) {
				tel = "010-" + fx_userTel2Tf.getText() + "-" + fx_userTel3Tf.getText();
			} else {
				tel = fx_userTel1Cb.getValue().toString() + "-" + fx_userTel2Tf.getText() + "-"
						+ fx_userTel3Tf.getText();
			}

			String email = "";
			if (fx_userEamil2Cb.getValue() == null) {
				email = fx_userEmail1Tf.getText() + "@" + "unknown.com";
			} else {
				email = fx_userEmail1Tf.getText() + "@" + fx_userEamil2Cb.getValue().toString();
			}
			uVo.setUserTel(tel);
			uVo.setUserEmail(email);

			vo.setProtocol(Protocol.UPDATE_USERINFO);
			vo.setuVo(uVo);
			System.out.println(uVo.toString());
			setMsgServer(vo);
			AlertController.alertCon(AlertType.INFORMATION, "알림", "수정", "정보를 갱신 했습니다.");
		}

	}

	@FXML
	public void userDelete(ActionEvent event) {
		// 확인을 누르면 참 , 취소를 누르면 거짓.
		boolean delete = AlertController.alertCon(AlertType.CONFIRMATION, "회원탈퇴", "주의", "정말로 회원탈퇴를 하시겠습니까?");
		if (delete) {
			String pwd = AlertController.dialogCon("회원 탈퇴", "비밀번호 확인", "사용자 비밀번호를 입력하세요.");
			vo.setProtocol(Protocol.ID_CHECK);
			vo.setUserId(fx_myIdT.getText());
			setMsgServer(vo);

			stop = false;
			Thread thread = new Thread() {
				@Override
				public void run() {
					while (!stop) {
						Platform.runLater(() -> {
							if (GetDataFromUserThread.getGetComparePwVo() != null) {
								System.out.println("hhh");
								deleteFunction(GetDataFromUserThread.getGetComparePwVo(), pwd);
								stop = true;
								return;
							}
						});
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
					}
				}
			};
			thread.setDaemon(true);
			thread.start();

		}

	}

	public void deleteFunction(ObjectVO vo, String pwd) {
		// 스레드가 더이상 객체를 참조하지 못하게하여 스레드를 종료시킨다.
		GetDataFromUserThread.setGetComparePwVo(null);
		if (vo.getUserPw().equals(pwd)) {
			AlertController.alertCon(AlertType.INFORMATION, "알림", "알림", "회원 탈퇴를 진행합니다.");
			vo.setProtocol(Protocol.DELETE_USER);
			vo.setUserId(fx_myIdT.getText());

			setMsgServer(vo);
			StageController.closeStage(fx_chatVbox);
		} else {
			AlertController.alertCon(AlertType.ERROR, "알림", "", "비밀번호가 일치하지 않습니다.");
		}
	}

	// 친구 추가 버튼을 눌렀을 때 동작.
	@FXML
	public void friendAction(ActionEvent event) {

		String searchId = AlertController.dialogCon("친구 검색", "검색할 친구의 아이디를 입력하세요.", "아이디 : ");
		System.out.println(searchId);
		if (searchId != null && searchId.length() > 0) {
			ObjectVO vo = new ObjectVO(Protocol.GET_UESRINFO, fx_myIdT.getText(), searchId, null);
			setMsgServer(vo);

			stop = false;
			Thread thread = new Thread() {
				@Override
				public void run() {
					while (!stop) {
						Platform.runLater(() -> {
							if (GetDataFromUserThread.getAddFriendVo() != null) {
								addFriend(GetDataFromUserThread.getAddFriendVo());
								stop = true;
							}
						});
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}

	}

	// 친구추가.
	public void addFriend(ObjectVO vo) {
		System.out.println("hihi```");
		ObjectVO pvo = vo;
		GetDataFromUserThread.setAddFriendVo(null);

		if (pvo.getProtocol().equals(Protocol.USER_EXIST)) {

			if (fList.contains(pvo.getReceiver())) {
				AlertController.alertCon(AlertType.ERROR, "알림", "", "이미 추가된 회원입니다.");
			} else if (pvo.getReceiver().equals(fx_myIdT.getText())) {
				AlertController.alertCon(AlertType.ERROR, "알림", "", "자신의 아이디를 친구로 추가할 수 없습니다.");

			} else {
				fList.add(pvo.getReceiver());
				myFriendList.clear();
				for (String tf : fList) {
					myFriendList.add(tf);
				}
				fx_friendList.setItems(fList);
				System.out.println("[chat Controller - addUser] 리스트에 친구가 추가되었습니다.");
			}

		} else if (pvo.getProtocol().equals(Protocol.CANNOT_FIND_USER)) {
			stop = true;
			AlertController.alertCon(AlertType.ERROR, "알림", "", "없는 사용자 입니다. 아이디를 다시 확인해주세요.");
			return;
		}
		
		
	}

	// 로그보여주는것.
	public void showLog(ActionEvent e) {
		System.out.println(Log.showLog());
		
	}

	// 리스트에서 우클릭시 보여질 메뉴들을 선언 및 이벤트처리한것.
	public void contextMenuView() {
		MenuItem menuItem = new MenuItem("삭제");
		MenuItem menuItem2 = new MenuItem("방 나가기");
		menuItem.setOnAction(new EventHandler<ActionEvent>() {

			// 친구삭제.
			@Override
			public void handle(ActionEvent event) {
				System.out.println(fx_friendList.getSelectionModel().getSelectedItem());
				String deleteFriend = fx_friendList.getSelectionModel().getSelectedItem();
				fx_friendList.getSelectionModel().clearSelection();
				if (deleteFriend != null) {
					if (AlertController.alertCon(AlertType.CONFIRMATION, "알림", "알림", deleteFriend + "를 삭제하시겠습니까?")) {
						ObjectVO deleteFri = new ObjectVO(Protocol.DELETE_FRIENDS, fx_myIdT.getText());
						ObjectVO deleteRoom = new ObjectVO(Protocol.EXIT_ROOM , fx_myIdT.getText() );
						deleteRoom.setRoomList(myRoomList);
						deleteFri.setReceiver(deleteFriend);
						setMsgServer(deleteFri);
						
						for(Room rn : myRoomList) {
							if(rn.getUsers().size() == 1 && rn.getUsers().get(0).equals(deleteFriend)) {
								deleteRoom.setReceiver(deleteFriend);
								deleteRoom.setRoomNo(rn.getRoomNo());
								deleteRoom.setRoom(rn);
								setMsgServer(deleteRoom);
							}
						}
						System.err.println(myRoomList);
					}

				}
			}

		});

		// 방 나가기.
		menuItem2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				deleteRoomNnumber = fx_roomList.getSelectionModel().getSelectedItem();
				System.out.println(fx_roomList.getSelectionModel().getSelectedItem());
				fx_roomList.getSelectionModel().clearSelection();
				roomOut();
			}

		});

		contextMenu.getItems().add(menuItem);
		contextMenu2.getItems().add(menuItem2);
	}

	// 파일전송!!
	@FXML
	public void sendFile(ActionEvent event) {
		file = fileChooser.showOpenDialog(chatStage);

		if (file != null) {
			boolean chk = AlertController.alertCon(AlertType.CONFIRMATION, "파일전송", "파일전송",
					fx_receiverT.getText() + "님에게 파일을 전송 요청을 하시겠습니까?");
			if (chk) {
				String tarr[] = fx_receiverT.getText().split(":");
				ObjectVO objVo = new ObjectVO(Protocol.SEND_FILE_REQUEST, fx_myIdT.getText(), tarr[1], "파일전송요청을 보냅니다.");
				objVo.setFileName(file.getName());
				connectServer.sendMsg(objVo);

			}

		}

	}

	// 내 채팅내용 지우기.
	@FXML
	public void clearChat(ActionEvent event) {
		System.out.println("userID : " + vo.getUserId());
		System.out.println("Rece ID : " + vo.getReceiver());
		vo.setProtocol(Protocol.CLEAR_CHAT_TEXT);
		vo.setRoomNo(roomNoMember);
		vo.setRoomList(myRoomList);
		setMsgServer(vo);
		fx_chatScrollPane.setVvalue(-1.0);
		fx_chatScrollPane.setHvalue(-1.0);
		fx_chatTa.setLayoutX(455.0);
		fx_chatTa.setLayoutY(450.0);
	}

	//유저 초대.
	@FXML
	public void inviteUser(ActionEvent e) {
		String tarr[] = fx_receiverT.getText().split(":");
//		int roomNumber = Integer.parseInt(tarr[0]);
		String inviUser = AlertController.dialogCon("Invite User", "초대", "초대할 상대의 아이디를 입력하세요.");
		if(inviUser.length() > 0) {
			//receiver에 초대할 사람 넣어서 보내기.
			if(tarr.length > 0) {
				//이미 다인인 방일때,
				ObjectVO inviteObj = new ObjectVO(Protocol.INVITE_USER,fx_myIdT.getText());
				Room rn = null;
				String newTitle = fx_receiverT.getText() + ":" + inviUser;
				fx_receiverT.setText(newTitle);
				for(Room inviRoom : myRoomList) {
					if(inviRoom.getRoomNo() == Integer.parseInt(tarr[0])) {
						rn = inviRoom;
					}
				}
				inviteObj.setRoomNo(Integer.parseInt(tarr[0]));
				inviteObj.setReceiver(inviUser);
				inviteObj.setRoom(rn);
			
				setMsgServer(inviteObj);
				
				
			}
//				else {
//				//1:1일땐 새롭게 방을 만들어야한다.
//				ObjectVO groupObj = new ObjectVO(Protocol.CREATE_GROUP_ROOM,fx_myIdT.getText());
//				groupObj.setRoomList(myRoomList);
//				groupObj.setReceiver(inviUser);
//				
//			}
		}

	}

	// 채팅방 나갈때,
	public void roomOut() {
		if (deleteRoomNnumber != null) {
			if (AlertController.alertCon(AlertType.CONFIRMATION, "알림", "알림!", "채팅방을 나가시겠습니까??")) {
				String tStr[] = deleteRoomNnumber.split(":");
				if (tStr.length == 2) {
					ObjectVO exitRoom = new ObjectVO(Protocol.EXIT_ROOM,fx_myIdT.getText());
					exitRoom.setRoomNo(Integer.parseInt(tStr[0]));
					exitRoom.setReceiver(tStr[1]);
					setMsgServer(exitRoom);

				}else {
					ObjectVO exitRoom = new ObjectVO(Protocol.EXIT_ROOM,fx_myIdT.getText());
					exitRoom.setRoomNo(Integer.parseInt(tStr[0]));
					exitRoom.setReceiver(null);
					Room groupRoom = null;
					for(Room rn : myRoomList) {
						if(rn.getRoomNo() == Integer.parseInt(tStr[0])) {
							groupRoom = rn;
						}
					}
					if(groupRoom != null) {
						exitRoom.setRoom(groupRoom);
						setMsgServer(exitRoom);
					}

				}
			}
		}

	}

}
