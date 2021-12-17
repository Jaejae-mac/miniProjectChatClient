package application.controller;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

//경고창을 띄울때 편하게 묶어서 관리하기 위한 클래스
public class AlertController {
	public static boolean alertCon(AlertType type, String title, String headerMessage, String contentMessage) {
		Alert alert = null;
		switch (type) {
		case CONFIRMATION:
			alert = new Alert(type);
			alert.setTitle(title);
			alert.setHeaderText(headerMessage);
			alert.setContentText(contentMessage);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				return true;
			} else if (result.get() == ButtonType.CANCEL) {
				alert.close();
				return false;
			}

		case ERROR:
			alert = new Alert(type);
			alert.setTitle(title);
			alert.setContentText(contentMessage);
			alert.showAndWait();
			break;

		case INFORMATION:
			alert = new Alert(type);
			alert.setTitle(title);
			alert.setHeaderText(headerMessage);
			alert.setContentText(contentMessage);
			alert.showAndWait();
			break;

		case NONE:
			alert = new Alert(type);
			alert.setTitle(title);
			alert.setContentText(contentMessage);
			alert.show();
			break;
		default:
			break;
		}
		return false;
	}

	public static String dialogCon(String title, String headerText, String contentText) {
		TextInputDialog dialog = new TextInputDialog();
		String searchId = null;
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);
		dialog.setContentText(contentText);
		dialog.showAndWait();

		searchId = dialog.getEditor().getText();
		System.out.println(searchId);
		return searchId;
	}
}
