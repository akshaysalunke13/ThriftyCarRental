package view;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import controller.VehicleController;
import exception.ReturnException;
import helpers.AlertDialog;
import helpers.DateTime;
import helpers.FileAccess;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

public class ReturnDialogViewController {

	private VehicleController modelController;
	@FXML
	private DatePicker returnDatePicker;
	@FXML
	private Button returnButton;

	public void initialize() {
		BooleanBinding returnBind = returnDatePicker.valueProperty().isNull();
		returnButton.disableProperty().bind(returnBind);
	}

	@FXML
	private void handleReturnButtonAction(ActionEvent event) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formattedDate = returnDatePicker.getValue().format(formatter);
		DateTime returnDate = FileAccess.parseDate(formattedDate);

		try {
			modelController.returnVehicle(returnDate);
			AlertDialog.showAlert(Alert.AlertType.INFORMATION, "Success! ", "Vehicle returned successfully.");
			goBack(event);

		} catch (ReturnException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Return Conditions Not Met!", e.getMessage());
		} catch (SQLException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Database Error! ", e.getMessage());
		}
	}

	@FXML
	private void handleCancelButtonAction(ActionEvent event) {
		goBack(event);
	}

	private void goBack(ActionEvent event) {
		Node source = (Node) event.getSource();
		source.getScene().getWindow().hide();
	}

	public VehicleController getModelController() {
		return modelController;
	}

	public void setModelController(VehicleController modelController) {
		this.modelController = modelController;
	}

}
