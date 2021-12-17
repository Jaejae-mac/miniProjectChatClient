package application.controller;


import application.protocol.Protocol;
import application.protocol.vo.ObjectVO;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

//스테이지를 열고 닫고를 담당할 클래스.
public class StageController {
	public static String makePath(String fileName) {
		return "/application/files/fxml/" + fileName + ".fxml";
	}
	
	public static void openStage(Parent root,String title, ConnectServer cs,String userId) {
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle(title);
		primaryStage.setResizable(false);
		primaryStage.show();
		//parent타입의 변수 root 가 먼저 root 로서 등록된후에 윈도우 종료 액션을 지정하려 하면 이미 지정되어 사용할 수 없다고 나온다.
		//따라서 등록되는 곳에서 스테이지 관련 윈도우 액션을 지정해주어야 한다.
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				if(AlertController.alertCon(AlertType.CONFIRMATION, "종료", "", "종료하시겠습니까?")) {
					if(primaryStage.getTitle().equals("Chat GUI")) {
//						sendRemove(cs);
						cs.sendMsg(new ObjectVO(Protocol.LOGOUT,userId));
					}
					Platform.exit();
				}else {
					event.consume();
				}
			}
		});
		
	}
	
	public static void closeStage(VBox vbox) {
		Stage stage = (Stage)vbox.getScene().getWindow();
		stage.close();
	}
}
