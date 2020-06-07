package model;

import helpers.DateTime;

public class Van extends Vehicle {

	// Constructor to initialize Van object
	public Van(String id, int mfd_year, String make, String model, DateTime last) {
		super(id, mfd_year, make, model, 15, "Available");
		this.setLastMaintenanceDate(last);

	}
}
