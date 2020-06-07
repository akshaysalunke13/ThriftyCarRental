package view;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import controller.VehicleController;
import exception.InputException;
import exception.RentException;
import helpers.AlertDialog;
import helpers.DateTime;
import helpers.FileAccess;
import helpers.DataValidator;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class RentDialogViewController {

	private VehicleController vehicleController;
	@FXML
	private TextField cusIdField;
	@FXML
	private DatePicker rentDatePicker;
	@FXML
	private TextField numDaysField;
	@FXML
	private Button rentButton;

	public void initialize() {
		setUpCusId();
		setUpNumDays();
		setUpRentButton();
	}

	//Customer id can be alpha numeric
	private void setUpCusId() {
		cusIdField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				String text = cusIdField.getText();

				if (!newValue && !text.isEmpty()) {
					try {
						DataValidator.validateAlphaNum(text);

					} catch (InputException e) {
						cusIdField.setText("");
						AlertDialog.showAlert(Alert.AlertType.ERROR, " Invalid Input! ", e.getMessage());
						cusIdField.requestFocus();
					}
				}
			}
		});

	}

	//Number of days should be integer
	private void setUpNumDays() {
		numDaysField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				String text = numDaysField.getText();
				if (!newValue && !text.isEmpty()) {
					try {

						DataValidator.validateNumber(text);
					} catch (InputException e) {

						numDaysField.setText("");
						AlertDialog.showAlert(Alert.AlertType.ERROR, " Invalid Input! ", e.getMessage());
						numDaysField.requestFocus();
					}
				}
			}
		});
	}

	private void setUpRentButton() {
		BooleanBinding rentBind = cusIdField.textProperty().isNotEmpty().and(rentDatePicker.valueProperty().isNotNull())
				.and(numDaysField.textProperty().isNotEmpty());
		rentButton.disableProperty().bind(rentBind.not());
	}

	@FXML
	private void handleRentButtonAction(ActionEvent event) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String formattedDate = rentDatePicker.getValue().format(format);

		DateTime rentDate = FileAccess.parseDate(formattedDate);

		int numOfRentDay = Integer.parseInt(numDaysField.getText());

		try {

			vehicleController.rent(cusIdField.getText(), rentDate, numOfRentDay);

			AlertDialog.showAlert(Alert.AlertType.INFORMATION, "Success!", "Vehicle rented successfully.");

			goBack(event);

		} catch (RentException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Rent Conditions Not Met!", e.getMessage());
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
		return vehicleController;
	}

	public void setModelController(VehicleController controller) {
		this.vehicleController = controller;
	}
}
