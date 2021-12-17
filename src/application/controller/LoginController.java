package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import application.log.Log;
import application.protocol.Protocol;
import application.protocol.vo.ObjectVO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

//로그인을 관리할 컨트롤러
public class LoginController implements Initializable {
	@FXML
	VBox fx_signInVbox;
	@FXML
	TextField account;
	@FXML
	PasswordField password;
	@FXML
	Button signup;
	@FXML
	Button signin;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.account.setStyle(
				 "-fx-border-width: 0 2 2 2, 2 0 0 0;\n" +
				 "-fx-border-radius: 6, 6;\n" +
				 "-fx-border-color: #FFA7A7, #FFA7A7;\n" +
				 "-fx-background-color: #EAEAEA;"		);
		this.password.setStyle(
				"-fx-border-width: 0 2 2 2, 2 0 0 0;\n" +
		        "-fx-border-radius: 6,6;\n" +
		        "-fx-border-color: #FFA7A7, #FFA7A7;\n" +
		        "-fx-background-color: #EAEAEA;"
		);
		password.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					signinLogic();
				}

			}
		});

	}

	@FXML
	public void signAction(ActionEvent e) {

		// 회원가입.
		if ((Button) e.getSource() == signup) {
			StageController.closeStage(fx_signInVbox);
			String path = StageController.makePath("signUpView");
			Parent root;
			try {
				root = FXMLLoader.load(getClass().getResource(path));
				StageController.openStage(root, "회원가입", null, null);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		// 로그인.
		if ((Button) e.getSource() == signin) {
			signinLogic();
		}

	}

	public void signinLogic() {

		System.out.println("signin");
		if (account.getText().length() >= 0 && password.getText().length() >= 0) {
			String userId = account.getText();
			String userPw = password.getText();

			// 아이디 존재여부 부터 판단하고, 존재한다면 UserVO 객체를 받아온다.
			ConnectServer connectServer = null;
			ObjectVO tvo = new ObjectVO(Protocol.LOGIN, userId, userPw);
			connectServer = new ConnectServer(Protocol.LOGIN, tvo);

			if (connectServer.isLoginFlag()) {
				String path = StageController.makePath("chatView");
				try {
					// 1. loader 를 통해 해당경로의 fxml 파일을 클래스로 읽어오고.
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource(path));
					Parent root = loader.load();

					// 2. 읽어온 경로의 파일을 열고,
					StageController.openStage(root, "Chat GUI", connectServer, userId);
//					StageController.setUserId(userId);

					// 3. 경로에 등록된 컨트롤러의 정보를 얻어오고
					ChatController controller = loader.getController();
					// 4. 컨트롤러의 세터를 이용하여 서버 객체를 전달.
					controller.setConnectServer(connectServer);

					// 5.현재 열려있는 창을 닫는다.
					StageController.closeStage(fx_signInVbox);
					Log.sbLog.append("로그인 하였습니다. - " + new Date() + "\n");
				} catch (IOException e) {
//					System.err.println("메인 채팅창을 오픈하다가 에러 발생. - loginController");
					Log.sbLog.append("로그인창을 오픈하다가 오류가 발생하였습니다. - " + new Date() + "\n");
					e.printStackTrace();
				}
			} else {
				AlertController.alertCon(AlertType.ERROR, "알림", "", "비밀번호를 다시확인해주세요.");
				password.requestFocus();
			}
		} else {
			AlertController.alertCon(AlertType.ERROR, "알림", "", "존재하지 않는 회원입니다.");
			password.clear();
			account.requestFocus();
		}
	}

}
