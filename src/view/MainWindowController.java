package view;

import java.io.FileNotFoundException;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.beans.value.ObservableValue;
import java.io.IOException;
import javafx.scene.layout.TilePane;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;

import model.Vehicle;
import exception.AddVehicleException;
import exception.ExportException;
import helpers.DatabaseAccess;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.event.*;

import java.util.Iterator;

import com.sun.prism.paint.Color;

import javafx.scene.control.ScrollPane;

import javafx.stage.WindowEvent;

import java.awt.Insets;
import java.io.File;
import javafx.scene.control.ChoiceBox;
import helpers.AlertDialog;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundFill;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import helpers.FileAccess;
import view.VehicleListViewController;

import javafx.fxml.FXMLLoader;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Alert.AlertType;

public class MainWindowController {

	@FXML
	private BorderPane mainPane;
	@FXML
	private StackPane centerView;
	@FXML
	private AnchorPane detailView;
	@FXML
	private ScrollPane scroll;
	@FXML
	private TilePane list;
	@FXML
	private ChoiceBox<String> vehicleType;
	@FXML
	private ChoiceBox<Integer> seatFilter;
	@FXML
	private ChoiceBox<String> statusFilter;
	@FXML
	private TextField makeFilter;

	@FXML
	private Label vehicleNumber;

	private ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
	private ObservableList<Vehicle> filteredVehicles = FXCollections.observableArrayList();

	public void initialize() {

		setUpVehicleTypeFilter();
		setUpSeatFilter();
		setUpStatusFilter();
		setUpVehicleList();
		setUpListView();
		refreshVehicleList();
	}

	private void setUpVehicleTypeFilter() {
		// Set vehicle types
		vehicleType.setItems(FXCollections.observableArrayList(null, "Car", "Van"));

		vehicleType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				//Option "Van" selected hence set number of seats to 15
				if (newValue.equals(2)) {
					seatFilter.setItems(FXCollections.observableArrayList(15));
					seatFilter.getSelectionModel().selectFirst();
				} else {
					seatFilter.setItems(FXCollections.observableArrayList(null, 4, 7));
				}
			}
		});

	}

	// Populate status list
	private void setUpStatusFilter() {
		statusFilter.setItems(FXCollections.observableArrayList(null, "Available", "Rented", "Under Maintenance"));
	}

	//Populate seats list
	private void setUpSeatFilter() {
		seatFilter.setItems(FXCollections.observableArrayList(null, 4, 7, 15));
	}

	//Refresh list
	private void setUpListView() {
		centerView.getChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(Change<? extends Node> c) {
				refreshVehicleList();
			}
		});
	}

	private void setUpVehicleList() {
		vehicles.addListener(new ListChangeListener<Vehicle>() {
			@Override
			public void onChanged(Change<? extends Vehicle> c) {
				updateFilteredVehicles();
			}
		});
	}

	private void refreshVehicleList() {
		fetchAllData();
		displayList();
	}

	private void fetchAllData() {
		//
		vehicles.clear();
		vehicles.addAll(DatabaseAccess.getAllVehicles());
	}

	private BorderPane createListItem(Vehicle vehicle) {
		BorderPane listItem = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("VehicleListView.fxml"));
			listItem = loader.load();
			VehicleListViewController controller = loader.getController();
			controller.setProperty(vehicle);
			controller.setParentCenterView(centerView);
			controller.setParentDetailView(detailView);
			controller.setParentBottomLabel(vehicleNumber);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return listItem;

	}

	private void displayList() {
		// Create list item for each vehicle and add to the view
		list.getChildren().clear();
		Iterator<Vehicle> it = filteredVehicles.iterator();
		while (it.hasNext()) {
			Vehicle property = it.next();
			list.getChildren().add(createListItem(property));
		}
		scroll.setVvalue(0);
		String info = String.format("Showing %1$s out of %2$s vehicle", filteredVehicles.size(), vehicles.size());
		vehicleNumber.setText(info);
	}

	private void updateFilteredVehicles() {
		// Reset the filtered vehicle list so the program can display correctly
		// vehicles according to user filters
		filteredVehicles.clear();
		Iterator<Vehicle> it = vehicles.iterator();
		while (it.hasNext()) {
			Vehicle v = it.next();
			if (matchsFilters(v)) {
				filteredVehicles.add(v);
			}
		}
	}

	private boolean matchsFilters(Vehicle vehicle) {
		String make = makeFilter.getText().toLowerCase();
		String type = vehicleType.getValue();
		Integer noOfSeats = seatFilter.getValue();
		String status = statusFilter.getValue();

		boolean makeMatch = make.isEmpty() || vehicle.getMake().toLowerCase().indexOf(make) != -1;
		boolean typeMatch = type == null || vehicle.getClass().getSimpleName().equals(type);
		boolean noOfSeatsMatch = noOfSeats == null || vehicle.getNoOfSeats() == noOfSeats;
		boolean statusMatch = status == null || vehicle.getStatus().equals(status);

		return makeMatch && typeMatch && noOfSeatsMatch && statusMatch;
	}

	//Handle APPLY button click
	@FXML
	private void handleApplyButtonAction(ActionEvent event) {
		updateFilteredVehicles();
		displayList();
	}

	//Handle RESET button click
	@FXML
	private void handleResetButtonAction(ActionEvent event) {
		resetFilters();
		refreshVehicleList();
	}

	private void resetFilters() {
		makeFilter.clear();
		vehicleType.getSelectionModel().selectFirst();
		statusFilter.getSelectionModel().selectFirst();
	}

	//Handle QUIT PROGRAM button click
	@FXML
	private void handleQuitButtonAction(ActionEvent event) {
		System.exit(0);
	}

	//Handle ADD VEHICLE option click
	@FXML
	private void handleAddVehicleButtonAction(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AddVehicleView.fxml"));
			Parent addVehicleView = loader.load();
			Stage stage = new Stage();
			stage.initOwner(mainPane.getScene().getWindow());
			stage.setOnHidden(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					refreshVehicleList();
				}
			});
			stage.setScene(new Scene(addVehicleView));
			stage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Handle IMPORT FILE option click
	@FXML
	private void handleImportButtonAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select the file to import");
		File file = fileChooser.showOpenDialog(new Stage());
		if (file == null) {
			return;
		}

		try {
			FileAccess.importFile(file);
			AlertDialog.showAlert(AlertType.INFORMATION, "Success!",
					"Successfully imported data from the chosen file!");

		} catch (SQLException | AddVehicleException e) {
			AlertDialog.showAlert(AlertType.ERROR, "Error!", e.getMessage());
		}
		refreshVehicleList();
	}

	//Handle EXPORT TO TEXT FILE option click
	@FXML
	private void handleExportButtonAction(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(new Stage());

		if (selectedDirectory == null) {
			return;
		}

		try {
			FileAccess.exportAll(selectedDirectory);
			AlertDialog.showAlert(AlertType.INFORMATION, "Success!",
					"Successfully exported all data to the chosen directory!");
		} catch (ExportException | FileNotFoundException e) {
			AlertDialog.showAlert(AlertType.ERROR, "Error!", e.getMessage());
		}
	}

}
