package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Iterator;

import controller.CarController;
import controller.VanController;
import controller.VehicleController;
import model.Car;
import model.Van;
import model.RentalRecord;
import model.Vehicle;

import model.RecordBean;

import helpers.AlertDialog;
import helpers.DatabaseAccess;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class VehicleDetailViewController {

	private Vehicle vehicle;
	private VehicleController controller;
	
	@FXML
	private StackPane parentCenterView;
	@FXML
	private AnchorPane parentDetailView;
	@FXML
	private Label parentBottomLabel;
	@FXML
	private ImageView image;
	@FXML
	private Label type;
	@FXML
	private TitledPane vehicleDetails;
	@FXML
	private Label status;
	@FXML
	private Label extra;

	@FXML
	private TableView<RecordBean> recordList;

	private ObservableList<RecordBean> tableContent = FXCollections.observableArrayList();
	@FXML
	private Button rentButton;
	@FXML
	private Button returnButton;
	@FXML
	private Button performMaintenanceButton;
	@FXML
	private Button completeMaintenanceButton;

	private ObservableList<RentalRecord> records = FXCollections.observableArrayList();

	public void initialize() {
		setUpButtonsForStatus();
		setUpRecordList();
	}

	private void setUpButtonsForStatus() {
		status.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				initButtons();
				if (newValue.equals("Available")) {
					returnButton.setDisable(true);
					completeMaintenanceButton.setDisable(true);
				} else if (newValue.equals("Rented")) {
					rentButton.setDisable(true);
					performMaintenanceButton.setDisable(true);
					completeMaintenanceButton.setDisable(true);
				} else if (newValue.equals("Under Maintenance")) {
					rentButton.setDisable(true);
					returnButton.setDisable(true);
					performMaintenanceButton.setDisable(true);
				}
			}

			private void initButtons() {
				rentButton.setDisable(false);
				returnButton.setDisable(false);
				performMaintenanceButton.setDisable(false);
				completeMaintenanceButton.setDisable(false);
			}
		});
	}

	private void setUpRecordList() {
		recordList.setItems(tableContent);

		recordList.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("KEY"));
		recordList.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("VALUE"));
		recordList.getColumns().get(0).prefWidthProperty().bind(recordList.widthProperty().multiply(0.4));
		recordList.getColumns().get(1).prefWidthProperty().bind(recordList.widthProperty().multiply(0.6));
	}

	@FXML
	private void handleRentButtonAction(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("RentDialogView.fxml"));
			Parent rentDialogView = loader.load();
			RentDialogViewController rdViewController = loader.getController();

			rdViewController.setModelController(controller);

			Stage stage = new Stage();
			Node source = (Node) event.getSource();
			stage.initOwner(source.getScene().getWindow());
			stage.setOnHidden(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					setVehicle(controller.getModel());
					refreshRecordList();
				}
			});
			stage.setScene(new Scene(rentDialogView));
			stage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleReturnButtonAction(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ReturnDialogView.fxml"));
			Parent returnDialogView = loader.load();
			ReturnDialogViewController viewController = loader.getController();
			viewController.setModelController(controller);

			Stage stage = new Stage();
			Node source = (Node) event.getSource();
			stage.initOwner(source.getScene().getWindow());
			stage.setOnHidden(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					setVehicle(controller.getModel());
					refreshRecordList();
				}
			});
			stage.setScene(new Scene(returnDialogView));
			stage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handlePerformMaintenanceButtonAction(ActionEvent event) {
		try {
			controller.performMaintenance();
			AlertDialog.showAlert(Alert.AlertType.INFORMATION, "Success!", "Vehicle is under maintenance now.");
			setVehicle(controller.getModel());
		} catch (SQLException e) {
			AlertDialog.showAlert(Alert.AlertType.ERROR, "Database Error!", e.getMessage());
		}
	}

	@FXML
	private void handleCompleteMaintenanceButtonAction(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("CompleteMaintenanceDialogView.fxml"));
			Parent completeDialogView = loader.load();
			CompleteMaintenanceDialogViewController viewController = loader.getController();
			viewController.setModelController(controller);
			Stage stage = new Stage();
			Node source = (Node) event.getSource();
			stage.initOwner(source.getScene().getWindow());
			stage.setOnHidden(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					setVehicle(controller.getModel());
				}
			});
			stage.setScene(new Scene(completeDialogView));
			stage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleGoBackButtonAction(ActionEvent event) {
		parentCenterView.getChildren().get(1).toBack();
		parentBottomLabel.setVisible(true);
		parentDetailView.getChildren().clear();
	}

	public Vehicle getProperty() {
		return vehicle;
	}

	public void setVehicle(Vehicle veh) {

		this.vehicle = veh;
		refreshRecordList();

		String typeValue = veh.getVehicleID().startsWith("C_") ? "Car" : "Van";
		type.setText(typeValue);

		String detail = veh.getMake() + " " + veh.getModel() + " " + veh.getMfdYear();
		vehicleDetails.setText(detail);

		status.setText(veh.getStatus());

		if (typeValue.equals("Car")) {

			controller = new CarController(veh);
			extra.setText("Seats: " + veh.getNoOfSeats());

		} else {
			controller = new VanController(veh);
			extra.setText("Last Maintenance: " + veh.getLastMaintenanceDate().toString());
		}
		setImage();
	}

	private void setImage() {
		String imagePath = "images/" + vehicle.getImgName();
		try {

			Image img = new Image(new FileInputStream(imagePath));
			image.setImage(img);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void refreshRecordList() {
		fetchAllRecords();
		displayRecords();
	}

	private void fetchAllRecords() {
		records.clear();
		records.addAll(DatabaseAccess.getAllRecordsForID(vehicle.getVehicleID()));
	}

	private void displayRecords() {
		tableContent.clear();
		Iterator<RentalRecord> it = records.iterator();
		while (it.hasNext()) {
			RentalRecord record = it.next();
			createRecordDetails(record);
		}
	}

	private void createRecordDetails(RentalRecord record) {
		tableContent.add(new RecordBean("Record ID:", record.getRecordID()));
		tableContent.add(new RecordBean("Rent Date::", record.getRentDate().getFormattedDate()));
		tableContent.add(new RecordBean("Estimated Return Date:", record.getExpectedReturnDate().toString()));

		if (record.getActualReturnDate() != null) {
			tableContent.add(new RecordBean("Actual Return Date:", record.getActualReturnDate().toString()));
			tableContent.add(new RecordBean("Rental Fee:", String.format("%.2f", record.getRentFee())));
			tableContent.add(new RecordBean("Late Fee:", String.format("%.2f", record.getLateFee())));
		}
		tableContent.add(new RecordBean("--------", "--------"));
	}

	public StackPane getParentCenterView() {
		return parentCenterView;
	}

	public void setParentCenterView(StackPane parentCenterView) {
		this.parentCenterView = parentCenterView;
	}

	public AnchorPane getParentDetailView() {
		return parentDetailView;
	}

	public void setParentDetailView(AnchorPane detailView) {
		this.parentDetailView = detailView;
	}

	public Label getParentBottomLabel() {
		return parentBottomLabel;
	}

	public void setParentBottomLabel(Label parentBottomLabel) {
		this.parentBottomLabel = parentBottomLabel;
	}

}
