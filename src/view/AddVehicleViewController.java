package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import model.Car;
import model.Van;
import model.Vehicle;
import exception.AddVehicleException;
import exception.InputException;
import helpers.AlertDialog;
import helpers.DatabaseAccess;
import helpers.DateTime;
import helpers.FileAccess;
import helpers.DataValidator;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddVehicleViewController {

	@FXML
	private TextField mfdYearField;
	@FXML
	private TextField vehicleID;
	@FXML
	private TextField makeField;
	@FXML
	private TextField modelField;
	@FXML
	private ChoiceBox<String> vehicleType;
	@FXML
	private Label seatCapacityLabel;
	@FXML
	private ChoiceBox<Integer> seatCapacityChoice;
	@FXML
	private Label lastMaintenanceDateLabel;
	@FXML
	private DatePicker datePicker;

	@FXML
	private Label descInfoLabel;
	@FXML
	private Label imageStatusLabel;
	private BufferedImage image;

	private String fileName;
	private String imageExtension;

	@FXML
	private Button addButton;

	@FXML
	public void initialize() {

		setUpVehicleIDField();
		setUpMfdYearField();
		setUpMakeField();
		setUpModelField();
		setUpVehicleTypes();
		setUpLastMaintenanceDate();
		setUpImage();
		setUpDescription();
		setUpAddButton();
	}

	// Set up vehicle id field.
	private void setUpVehicleIDField() {
		// TODO Auto-generated method stub
		vehicleID.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				String text = vehicleID.getText();

				if (!newValue && !text.isEmpty()) {
					try {
						//Validate ID for vehicle
						DataValidator.validateVehicleID(text);

					} catch (InputException e) {
						vehicleID.setText("");
						AlertDialog.showAlert(Alert.AlertType.ERROR, "Invalid Input!", e.getMessage());
						vehicleID.requestFocus();
					} finally {

						if (text.startsWith("C_")) {
							// ID starts with C_ for car
							vehicleType.setItems(FXCollections.observableArrayList("Car"));
							vehicleType.getSelectionModel().selectFirst();
						} else if (text.startsWith("V_")) {
							// ID starts with V_
							vehicleType.setItems(FXCollections.observableArrayList("Van"));
							vehicleType.getSelectionModel().selectFirst();
						}
					}
				} else if (text.isEmpty()) {
					vehicleType.setItems(FXCollections.observableArrayList("Car", "Van"));
				}
			}
		});

	}

	@FXML
	private void handleCancelButtonAction(ActionEvent event) {
		goBack(event);
	}

	// Return to parent screen
	private void goBack(ActionEvent event) {
		Node source = (Node) event.getSource();
		source.getScene().getWindow().hide();
	}

	// Mfd year field should contain numbers only
	private void setUpMfdYearField() {
		mfdYearField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				String text = mfdYearField.getText();

				if (!newValue && !text.isEmpty()) {
					try {
						DataValidator.validateNumber(text);

					} catch (InputException e) {
						mfdYearField.setText("");
						AlertDialog.showAlert(Alert.AlertType.ERROR, "Invalid Input!", e.getMessage());
						mfdYearField.requestFocus();
					}
				}
			}
		});
	}

	// Make field should be text only
	private void setUpMakeField() {
		makeField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				String text = makeField.getText();
				if (!newValue && !text.isEmpty()) {
					try {
						DataValidator.validateText(text);
					} catch (InputException e) {
						makeField.setText("");
						AlertDialog.showAlert(Alert.AlertType.ERROR, "Invalid Input!", e.getMessage());
						makeField.requestFocus();
					}
				}
			}
		});
	}

	// Model should contain text and numbers only
	private void setUpModelField() {
		modelField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				String text = modelField.getText();
				if (!newValue && !text.isEmpty()) {
					try {

						DataValidator.validateAlphaNum(text);
					} catch (InputException e) {
						modelField.setText("");
						AlertDialog.showAlert(Alert.AlertType.ERROR, "Invalid Input!", e.getMessage());
						modelField.requestFocus();
					}
				}
			}
		});
	}

	// Setup seats according to vehicle type selected
	private void setUpVehicleTypes() {
		vehicleType.setItems(FXCollections.observableArrayList("Car", "Van"));

		vehicleType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldString, String newString) {
				// TODO Auto-generated method stub
				if (newString.equals("Car")) {

					// A car has only 4 or 7 seats
					seatCapacityChoice.setItems(FXCollections.observableArrayList(4, 7));
					seatCapacityLabel.setVisible(true);
					seatCapacityChoice.setVisible(true);
					lastMaintenanceDateLabel.setVisible(false);

					datePicker.setVisible(false);
					datePicker.setValue(null);
				} else {

					lastMaintenanceDateLabel.setVisible(true);
					datePicker.setVisible(true);

					// A van always has 15 seats
					seatCapacityChoice.setItems(FXCollections.observableArrayList(15));
					seatCapacityLabel.setVisible(true);
					seatCapacityChoice.setVisible(true);
				}
			}

		});
	}

	private void setUpLastMaintenanceDate() {
		lastMaintenanceDateLabel.setVisible(false);
		datePicker.setVisible(false);
	}

	private void setUpImage() {
		imageStatusLabel.setVisible(false);
	}

	private void setUpDescription() {
		descInfoLabel.setVisible(false);

	}

	// Function to enable ADD button only when all necessary fields are completed.
	private void setUpAddButton() {
		BooleanBinding addVehicleBind = vehicleID.textProperty().isNotEmpty().and(mfdYearField.textProperty()
				.isNotEmpty().and(makeField.textProperty().isNotEmpty()).and(modelField.textProperty().isNotEmpty())
				.and(vehicleType.valueProperty().isNotNull())
				.and(seatCapacityChoice.valueProperty().isNotNull().or(datePicker.valueProperty().isNotNull())));

		addButton.disableProperty().bind(addVehicleBind.not());

	}

	@FXML
	private void handleUploadButtonAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select the vehicle image to upload.");
		
		// Allow image upload only
		fileChooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.JPG", "*.jpeg", "*.JPEG", "*.png", "*.PNG"));
		File file = fileChooser.showOpenDialog(new Stage());
		if (file == null) {
			return;
		}
		try {
			image = ImageIO.read(file);
			fileName = file.getName();
			//Get file extension required later on
			imageExtension = fileName.substring(fileName.lastIndexOf(".") + 1, file.getName().length());
			AlertDialog.showAlert(Alert.AlertType.INFORMATION, "Success!", "Image uploaded successfully.");
			imageStatusLabel.setVisible(true);
		} catch (IOException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Error!", e.getMessage());
		}
	}

	@FXML
	private void handleAddVehicleButtonAction(ActionEvent event) {
		String id = vehicleID.getText();
		String mfdYear = mfdYearField.getText();
		String make = makeField.getText();
		String model = modelField.getText();
		String type = vehicleType.getSelectionModel().getSelectedItem();

		if (type.equals("Car")) {

			addCar(id, mfdYear, make, model);

		} else if (type.equals("Van")) {

			addVan(id, mfdYear, make, model);
		}
		goBack(event);
	}

	//Create and add new Car to database
	private void addCar(String id, String mfdYear, String make, String model) {
		Vehicle newCar;
		int mfd = Integer.parseInt(mfdYear);

		int seats = seatCapacityChoice.getValue();

		newCar = new Car(id, mfd, make, model, seats);

		if (image != null) {
			newCar.setImgName(newCar.getVehicleID() + "." + imageExtension);
			saveImage(newCar.getImgName());
		}

		try {
			DatabaseAccess.checkVehicleExists(newCar.getVehicleID());
			DatabaseAccess.insertVehicle(newCar);
			AlertDialog.showAlert(Alert.AlertType.INFORMATION, "Success!", "Car added successfully.");
		} catch (AddVehicleException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Duplicate Car ID!", e.getMessage());
		} catch (SQLException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Database write Error!", e.getMessage());
		}
	}

	//Create and add Van to database
	private void addVan(String id, String mfdYear, String make, String model) {

		Vehicle newVan;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formattedDate = datePicker.getValue().format(formatter);

		DateTime lastMaintenanceDate = FileAccess.parseDate(formattedDate);

		newVan = new Van(id, Integer.parseInt(mfdYear), make, model, lastMaintenanceDate);
		if (image != null) {
			newVan.setImgName(newVan.getVehicleID() + "." + imageExtension);
			saveImage(newVan.getImgName());
		}

		try {
			DatabaseAccess.checkVehicleExists(newVan.getVehicleID());
			DatabaseAccess.insertVehicle(newVan);
			AlertDialog.showAlert(Alert.AlertType.INFORMATION, "Success!", "Van added successfully.");
		} catch (AddVehicleException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Duplicate Van ID!", e.getMessage());
		} catch (SQLException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Error!", e.getMessage());
		}
	}

	// Save uploaded image to /images folder
	private void saveImage(String fileName) {
		String path = "images/" + fileName;
		File saveFile = new File(path);
		try {
			ImageIO.write(image, imageExtension, saveFile);
		} catch (IOException e) {
			AlertDialog.showAlert(Alert.AlertType.WARNING, "Warning!",
					"There was a problem saving image. Vehicle saved with default no image");
		} catch (Exception e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Error!", e.getMessage());
		}
	}
}
