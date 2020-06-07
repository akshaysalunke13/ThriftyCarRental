package model;

import helpers.DateTime;

public class RentalRecord {

	private String recordID;
	private String custID;
	private String vehicleID;
	private DateTime rentDate;
	private DateTime expectedReturnDate;
	private DateTime actualReturnDate;
	private float rentFee;
	private float lateFee;

	// Constructor to create new rental record.
	public RentalRecord(String vehicleID, String cID, DateTime rentDate, DateTime expectedReturn) {
		super();
		this.vehicleID = vehicleID;
		this.custID = cID;
		this.recordID = vehicleID + "_" + cID + "_" + expectedReturn.getEightDigitDate();
		this.rentDate = rentDate;
		this.expectedReturnDate = expectedReturn;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getVehicleID() {
		return vehicleID;
	}

	public void setVehicleID(String vehicleID) {
		this.vehicleID = vehicleID;
	}

	public String getRecordID() {
		return recordID;
	}

	public DateTime getRentDate() {
		return rentDate;
	}

	public DateTime getExpectedReturnDate() {
		return expectedReturnDate;
	}

	public DateTime getActualReturnDate() {
		return actualReturnDate;
	}

	public void setActualReturnDate(DateTime actual_return) {
		this.actualReturnDate = actual_return;
	}

	public float getRentFee() {
		return rentFee;
	}

	public void setRentFee(float rent_fee) {
		this.rentFee = rent_fee;
	}

	public float getLateFee() {
		return lateFee;
	}

	public void setLateFee(float late_fee) {
		this.lateFee = late_fee;
	}

	@Override
	public String toString() {

		String stringToReturn = recordID + ":" + rentDate.getFormattedDate() + ":"
				+ expectedReturnDate.getFormattedDate() + ":";
		
		if (actualReturnDate != null) {
			// Formatting float decimal to 2 places.
			stringToReturn += actualReturnDate.getFormattedDate() + ":" + String.format("%.2f", rentFee) + ":"
					+ String.format("%.2f", lateFee);
		} else {
			// Vehicle not yet returned. Hence add "none" to string.
			stringToReturn += "none:none:none";
		}
		return stringToReturn;
	}

	public String getDetails() {

		String stringToReturn = "Record ID:\t" + recordID + "\nRent Date:\t" + rentDate.toString()
				+ "\nEstimated Return Date:\t" + expectedReturnDate.toString();
		if (actualReturnDate == null) {
			return stringToReturn;
		} else {
			stringToReturn = stringToReturn + "\nActual Return Date:\t" + actualReturnDate.toString()
					+ "\nRental Fee:\t" + String.valueOf(rentFee) + "\nLate Fee:\t" + Float.toString(lateFee);
		}
		return stringToReturn;
	}

}
