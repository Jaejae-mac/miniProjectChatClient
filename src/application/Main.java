package application;
	
import java.io.IOException;

import application.controller.AlertController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/application/files/fxml/signInView.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("KGITBANK MY TALK");
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}

		//최초 시작 로그인 페이지의 종료버튼 클릭시 프로그램 완전종료를 위한 이벤트.
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if(AlertController.alertCon(AlertType.CONFIRMATION, "종료", "", "종료하시겠습니까?")) {
					Platform.exit();
				}else {
					event.consume();
				}
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}






