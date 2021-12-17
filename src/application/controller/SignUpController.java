package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.jdbc.repository.vo.UserTableVO;
import application.log.Log;
import application.protocol.Protocol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class SignUpController implements Initializable {
	@FXML
	private VBox fx_signupVbox;

	@FXML
	private TextField user_id;
	@FXML
	private Button id_check;

	@FXML
	private PasswordField user_pw;
	@FXML
	private PasswordField user_pw_check;

	@FXML
	private TextField user_name;

	@FXML
	private ComboBox<String> user_tel1;
	@FXML
	private TextField user_tel2;
	@FXML
	private TextField user_tel3;

	@FXML
	private TextField user_email1;
	@FXML
	private ComboBox<String> user_email2;

	@FXML
	private Button cancel;
	@FXML
	private Button signup;

	private ObservableList<String> telList = FXCollections.observableArrayList("010", "011", "016", "019", "018");
	private ObservableList<String> dotComList = FXCollections.observableArrayList("naver.com", "hanmail.net",
			"daum.net", "hotmail.com", "gamil.com", "icloud.com", "nate.com");

	// 중복체크 버튼을 클릭하면 확인용도로 사용.
	private boolean flag = false;

	private ConnectServer connectServer;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		user_tel1.setItems(telList);
		user_email2.setItems(dotComList);

		user_id.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				if (flag) {
					user_id.clear();
					id_check.setDisable(false);
					flag = false;
				}
			}

		});

	}

	@FXML
	public void idCheck(ActionEvent event) {

		String id = user_id.getText();
		if (id.length() < 5) {
			Log.sbLog.append("[알림] 아이디는 5자리이상 입력해야 합니다.\n");
			AlertController.alertCon(AlertType.ERROR, "알림", "", "아이디는 5자리이상 입력해야 합니다.");
			flag = false;
			user_id.clear();
			id_check.setDisable(false);
			user_id.requestFocus();
		} else {
			connectServer = new ConnectServer(Protocol.ID_CHECK, id);
			UserTableVO uvo = connectServer.getVo().getuVo();

			if (uvo == null) {
				Log.sbLog.append("[알림] 사용가능한 아이디 입니다.\n");
				flag = true;
				id_check.setDisable(true);
				user_pw.requestFocus();
			} else {
				Log.sbLog.append("[알림] 사용 불가능한 아이디 입니다.\n");

				AlertController.alertCon(AlertType.ERROR, "알림", "", "사용할 수 없는 아이디입니다.");
				flag = false;
				user_id.clear();
				id_check.setDisable(false);
				user_id.requestFocus();
			}
		}
	}

	@FXML
	public void cancel(ActionEvent e) {
		System.out.println(Log.showLog());

		// 회원가입창 닫기.
		StageController.closeStage(fx_signupVbox);
		// 닫힌 후 로그인창 다시 열기.
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource(StageController.makePath("signinView")));
			StageController.openStage(root, "로그인", null, null);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@FXML
	public void signUp(ActionEvent e) {
		if (flag) {
			String id = user_id.getText();
			String password = user_pw.getText();
			String password_confirm = user_pw_check.getText();
			String name = user_name.getText();

			String tel = "";

			if (user_tel1.getValue() == null) {
				tel = "010-" + user_tel2.getText() + "-" + user_tel3.getText();
			} else {
				tel = user_tel1.getValue().toString() + "-" + user_tel2.getText() + "-" + user_tel3.getText();
			}

			String email = "";
			if (user_email2.getValue() == null) {
				email = user_email1.getText() + "@" + "unknown.com";
			} else {
				email = user_email1.getText() + "@" + user_email2.getValue().toString();
			}

			if (AlertController.alertCon(AlertType.CONFIRMATION, "회원가입", "회원가입을 진행합니다.", "진행하시겠습니까?")) {

				// 비밀번호 가 서로 일치하는지 확인후 일치하지 않으면 경고를 보인후 끝냄.
				if (!password.equals(password_confirm)) {
					AlertController.alertCon(AlertType.ERROR, "알림", "", "비밀번호가 일치하지 않습니다.");
					return;
				}
				
				if(name.length() == 0 || tel.length() < 13 || user_email1.getText().length() == 0) {
					AlertController.alertCon(AlertType.ERROR, "알림", "", "이름, 전화번호, 이메일을 정확히 입력해주세요.");
					user_name.requestFocus();
					return;
				}else {
					UserTableVO uvo = new UserTableVO(id, password, name, tel, email);
					connectServer = new ConnectServer(Protocol.SIGNUP, uvo);
				}

				// 회원가입창 닫기.
				StageController.closeStage(fx_signupVbox);
				// 닫힌 후 로그인창 다시 열기.
				Parent root;
				try {
					root = FXMLLoader.load(getClass().getResource(StageController.makePath("signinView")));
					StageController.openStage(root, "로그인", null, null);
				} catch (IOException ioe) {
					System.err.println("< 회원가입 완료후 로그인 창을 여는중 에러발생. > - SignUpController");
					ioe.printStackTrace();
				}
			} else {
				return;
			}
		} else {
			AlertController.alertCon(AlertType.ERROR, "알림", "", "아이디 중복체크를 해야합니다.");
			id_check.requestFocus();
		}
	}

}
