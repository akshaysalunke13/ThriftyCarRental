package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import model.Vehicle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class VehicleListViewController {

	private Vehicle vehicle;
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
	private Label vehicleDetails;
	@FXML
	private Label status;
	@FXML
	private Label other;

	@FXML
	private void handleViewButtonAction(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("VehicleDetailView.fxml"));
		TitledPane detail = null;
		try {
			detail = loader.load();
			VehicleDetailViewController controller = loader.getController();
			controller.setVehicle(this.vehicle);
			controller.setParentCenterView(parentCenterView);
			controller.setParentDetailView(parentDetailView);
			controller.setParentBottomLabel(parentBottomLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		AnchorPane.setTopAnchor(detail, 0.0);
		AnchorPane.setBottomAnchor(detail, 0.0);
		AnchorPane.setLeftAnchor(detail, 0.0);
		AnchorPane.setRightAnchor(detail, 0.0);
		parentDetailView.getChildren().add(detail);
		parentCenterView.getChildren().get(0).toFront();
		parentBottomLabel.setVisible(false);
	}

	public Vehicle getProperty() {
		return vehicle;
	}

	public void setProperty(Vehicle veh) {
		this.vehicle = veh;

		String typeValue = veh.getVehicleID().startsWith("C_") ? "Car" : "Van";
		type.setText(typeValue);

		String detail = veh.getMake() + " " + veh.getModel() + " " + veh.getMfdYear();
		vehicleDetails.setText(detail);

		status.setText(veh.getStatus());

		if (typeValue.equals("Car")) {
			other.setText("Number of Seats: " + veh.getNoOfSeats());
		} else {
			other.setText("Last Maintenance: " + veh.getLastMaintenanceDate().toString());
		}

		String imagePath = "images/" + veh.getImgName();

		try {

			Image img = new Image(new FileInputStream(imagePath));
			image.setImage(img);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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