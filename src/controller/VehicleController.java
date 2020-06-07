package controller;

import java.sql.SQLException;

import helpers.DatabaseAccess;

import exception.RentException;
import exception.ReturnException;
import helpers.DateTime;
import model.RentalRecord;
import model.Vehicle;

public abstract class VehicleController implements Rentable, Maintainable {

	private Vehicle model;
	private RentalRecord record;

	public VehicleController(Vehicle model) {
		super();
		this.model = model;
	}

	public Vehicle getModel() {
		return model;
	}

	public void setModel(Vehicle model) {
		this.model = model;
	}

	@Override
	public void performMaintenance() throws SQLException {
		// TODO Auto-generated method stub
		model.setStatus("Under Maintenance");

		DatabaseAccess.updateVehicle(model);
	}

	//Complete maintenance for vehicle. Don't insert date for Car objects
	@Override
	public void completeMaintenance(DateTime completeDate) throws SQLException {
		// TODO Auto-generated method stub
		model.setStatus("Available");
		
		if(model.getVehicleID().startsWith("V_"))
			model.setLastMaintenanceDate(completeDate);

		DatabaseAccess.updateVehicle(model);
	}

	// Check if date is in past
	public void checkRentDate(DateTime rentDate) throws RentException {

		DateTime now = new DateTime();
		boolean isInthePast = DateTime.diffDays(rentDate, now) < -1;
		if (isInthePast) {
			throw new RentException("The rent date cannot be in the past.");
		}
	}

	// Check vehicle specific requirements. Implemented in inherited classes.
	public abstract void checkRequirements(DateTime rentDate, int numOfDays) throws RentException;

	//Rent vehicle
	@Override
	public void rent(String customerId, DateTime rentDate, int numOfRentDay) throws RentException, SQLException {

		checkRentDate(rentDate);
		checkRequirements(rentDate, numOfRentDay);

		model.setStatus("Rented");

		DatabaseAccess.updateVehicle(model);
		DateTime estReturnDate = new DateTime(rentDate, numOfRentDay);

		RentalRecord record = new RentalRecord(model.getVehicleID(), customerId, rentDate, estReturnDate);
		DatabaseAccess.insertRentalRecordRow(record);
	}

	// Return vehicle
	@Override
	public void returnVehicle(DateTime returnDate) throws ReturnException, SQLException {

		record = DatabaseAccess.getLatestRentalRecordForID(model.getVehicleID());

		checkReturnDate(returnDate);
		calculateFees(returnDate);

		DatabaseAccess.updateRentalRecordRow(record);

		model.setStatus("Available");

		DatabaseAccess.updateVehicle(model);
	}

	// Calculate rent and late fees.
	private void calculateFees(DateTime actualReturnDate) {
		calculateRental(actualReturnDate);
		calculateLateRental(actualReturnDate);
	}

	// Calculate Late Fees
	private void calculateLateRental(DateTime actualReturnDate) {
		// TODO Auto-generated method stub
		DateTime estimatedReturn = record.getExpectedReturnDate();

		if (DateTime.diffDays(actualReturnDate, estimatedReturn) <= 0) {
			record.setLateFee(0.0f);
		} else {
			int daysExtra = DateTime.diffDays(actualReturnDate, estimatedReturn);
			record.setLateFee(daysExtra * getLateRate());
		}
	}

	// Calculate rent fees
	private void calculateRental(DateTime actualReturnDate) {
		// TODO Auto-generated method stub
		DateTime rentDate = record.getRentDate();
		DateTime estimatedReturn = record.getExpectedReturnDate();

		// Vehicle returned on or before expected return date.
		if (DateTime.diffDays(actualReturnDate, estimatedReturn) <= 0) {
			int days = DateTime.diffDays(actualReturnDate, rentDate);
			record.setRentFee(days * getRentalRate());
		} else {
			int days = DateTime.diffDays(estimatedReturn, rentDate);
			record.setRentFee(days * getRentalRate());
		}
	}

	// Check if return date is before rent date
	private void checkReturnDate(DateTime returnDate) throws ReturnException {
		// TODO Auto-generated method stub
		DateTime rent = record.getRentDate();

		if (DateTime.diffDays(returnDate, rent) < 0) {
			throw new ReturnException("The return date is before rent date.");
		}

		record.setActualReturnDate(returnDate);
	};

	// Methods to get rates of specific vehicle. To be implemented for each type of vehicle
	public abstract float getRentalRate();

	public abstract float getLateRate();
}
