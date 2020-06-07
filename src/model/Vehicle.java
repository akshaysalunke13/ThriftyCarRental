package model;

import java.util.ArrayList;
import helpers.DateTime;

public abstract class Vehicle {

	// Unique vehicle ID
	private String vehicleID;
	private int mfdYear;
	private String make;
	private String model;
	private int noOfSeats;
	
	// Possible value - Available, Rented, Under Maintenance
	private String status;
	
	// Holds current incomplete rental record.
	private RentalRecord currentRecord;
	private DateTime lastMaintenanceDate;
	
	// Rental history of last 10 records.
	private ArrayList<RentalRecord> rentalHistory = new ArrayList<RentalRecord>(10);
	private String imgName;

	public Vehicle(String id, int mfdYear, String make, String model, int seats, String status) {
		this.vehicleID = id;
		this.mfdYear = mfdYear;
		this.make = make;
		this.model = model;
		this.noOfSeats = seats;

		this.status = status; // Status available when vehicle is created
		this.imgName = "no_image.jpg";

	}

	public int getMfdYear() {
		return mfdYear;
	}

	public void setMfdYear(int mfdYear) {
		this.mfdYear = mfdYear;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public void setVehicleID(String vehicleID) {
		this.vehicleID = vehicleID;
	}

	public void setRentalHistory(ArrayList<RentalRecord> rentalHistory) {
		this.rentalHistory = rentalHistory;
	}

	public DateTime getLastMaintenanceDate() {
		return lastMaintenanceDate;
	}

	public void setLastMaintenanceDate(DateTime date) {
		this.lastMaintenanceDate = date;
	}

	public RentalRecord getCurrentRecord() {
		return currentRecord;
	}

	public void setCurrentRecord(RentalRecord current) {
		this.currentRecord = current;
	}

	public String getVehicleID() {
		return vehicleID;
	}

	public int getNoOfSeats() {
		return noOfSeats;
	}

	public void setNoOfSeats(int seat_capacity) {
		this.noOfSeats = seat_capacity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<RentalRecord> getRentalHistory() {
		return rentalHistory;
	}

	// Update rent history with actual return date, calculated rental and late fees.
	public void updateHistory(DateTime returnDate, float rentFee, float lateFee) {

		this.getCurrentRecord().setRentFee(rentFee);
		this.getCurrentRecord().setActualReturnDate(returnDate);
		this.getCurrentRecord().setLateFee(lateFee);

		if (rentalHistory.size() >= 10) {
			rentalHistory.remove(0);

		}
		rentalHistory.add(currentRecord);
		this.setCurrentRecord(null);
	}

	@Override
	public String toString() {
		String stringToReturn = this.vehicleID + ":" + this.mfdYear + ":" + this.make + ":" + this.model + ":"
				+ this.noOfSeats + ":" + this.status;
		if (this instanceof Van) {
			stringToReturn += ":" + this.lastMaintenanceDate;
		}

		stringToReturn += ":" + this.getImgName();
		return stringToReturn;
	}

	public String getDetails() {
		String detailsToReturn = "";
		detailsToReturn = "Vehicle ID:\t\t" + this.vehicleID + "\n";
		detailsToReturn += "Year:\t\t\t" + this.mfdYear + "\n";
		detailsToReturn += "Make:\t\t\t" + this.make + "\n";
		detailsToReturn += "Model:\t\t\t" + this.model + "\n";
		detailsToReturn += "Number of seats:\t" + this.noOfSeats + "\n";
		detailsToReturn += "Status:\t\t\t" + this.status + "\n";

		if (this instanceof Van) {
			detailsToReturn += "Last maintenance date:\t" + this.lastMaintenanceDate.getFormattedDate() + "\n";
		}

		detailsToReturn += "RENTAL RECORD:\t";

		// Vehicle never rented
		if (currentRecord == null && rentalHistory.size() == 0) {
			detailsToReturn += "EMPTY\n";
		}

		// Vehicle currently rented
		if (currentRecord != null) {
			detailsToReturn += "\n";
			detailsToReturn += "Record ID:\t" + this.currentRecord.getRecordID() + "\n";
			detailsToReturn += "Rent Date:\t" + this.currentRecord.getRentDate().getFormattedDate() + "\n";
			detailsToReturn += "Estimated return date:\t"
					+ this.currentRecord.getExpectedReturnDate().getFormattedDate() + "\n";
		}

		// Vehicle not currently rented but has rent history
		if (rentalHistory.size() != 0) {
			
			detailsToReturn += "\n--------------------------------------\n";
			
			for (int i = rentalHistory.size() - 1; i >= 0; i--) {
				
				detailsToReturn += "Record ID:\t\t" + this.rentalHistory.get(i).getRecordID() + "\n";
				detailsToReturn += "Rent Date:\t\t" + this.rentalHistory.get(i).getRentDate().getFormattedDate() + "\n";
				detailsToReturn += "Estimated return date:\t"
						+ this.rentalHistory.get(i).getExpectedReturnDate().getFormattedDate() + "\n";
				detailsToReturn += "Actual return date:\t"
						+ this.rentalHistory.get(i).getActualReturnDate().getFormattedDate() + "\n";
				detailsToReturn += "Rental fee:\t\t" + String.format("%.2f", this.rentalHistory.get(i).getRentFee())
						+ "\n";
				detailsToReturn += "Late fee:\t\t" + String.format("%.2f", this.rentalHistory.get(i).getLateFee())
						+ "\n";
			}
		}

		return detailsToReturn;
	}

}
