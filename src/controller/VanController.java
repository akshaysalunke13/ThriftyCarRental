package controller;

import exception.RentException;
import helpers.DateTime;
import model.Vehicle;

public class VanController extends VehicleController {

	// Constant values for rental rate
	private final float RENTAL = 235;
	private final float LATE_RENTAL = 299;

	public VanController(Vehicle model) {
		super(model);
	}

	@Override
	public void checkRequirements(DateTime rentDate, int numOfDays) throws RentException {

		DateTime returnDate = new DateTime(rentDate, numOfDays);
		DateTime nextMaintenance = new DateTime(getModel().getLastMaintenanceDate(), 12);
		//
		if (DateTime.diffDays(returnDate, nextMaintenance) > 0) {
			throw new RentException("Return date exceeds the next maintenance date");
		}

		if (numOfDays < 1) {
			throw new RentException("Van has to be rented for minimum 1 day.");
		}
	}

	@Override
	public float getRentalRate() {
		
		return RENTAL;
	}

	@Override
	public float getLateRate() {
		
		return LATE_RENTAL;
	}

}
