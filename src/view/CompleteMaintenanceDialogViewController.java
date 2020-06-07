package view;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import controller.VehicleController;
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

public class CompleteMaintenanceDialogViewController {
	private VehicleController vehicleController;
	@FXML
	private DatePicker completionDatePicker;
	@FXML
	private Button completeButton;

	public void initialize() {
		BooleanBinding completeBind = completionDatePicker.valueProperty().isNull();
		completeButton.disableProperty().bind(completeBind);
	}

	@FXML
	private void handleCompleteButtonAction(ActionEvent event) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formattedDate = completionDatePicker.getValue().format(formatter);
		
		DateTime completionDate = FileAccess.parseDate(formattedDate);

		try {
			vehicleController.completeMaintenance(completionDate);
			
			AlertDialog.showAlert(Alert.AlertType.INFORMATION, "Success! ", "Maintenance completed successfully.");
			goBack(event);

		} catch (SQLException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Database Error!", e.getMessage());
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
		return vehicleController;
	}

	public void setModelController(VehicleController modelController) {
		this.vehicleController = modelController;
	}

}
