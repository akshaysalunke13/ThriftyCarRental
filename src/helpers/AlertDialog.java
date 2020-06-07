package helpers;

import javafx.scene.control.Alert;

public class AlertDialog {

	// Method to create dialog box with specified title and message
	public static void showAlert(Alert.AlertType type, String title, String message) {

		Alert newAlert = new Alert(type);
		newAlert.setTitle(title);
		newAlert.setHeaderText(null);
		newAlert.setContentText(message);
		newAlert.showAndWait();
	}
}