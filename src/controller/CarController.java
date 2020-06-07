package controller;

import exception.RentException;
import helpers.DateTime;
import model.Vehicle;

public class CarController extends VehicleController {

	// Rental rate fixed constants.
	private final float SEAT_4_RENTAL = 78;
	private final float SEAT_7_RENTAL = 113;
	private final float LATE_PERCENT = 1.25f;

	public CarController(Vehicle model) {
		super(model);
	}

	@Override
	public void checkRequirements(DateTime rentDate, int numOfDays) throws RentException {

		// Check if rent duration is less than 14 as specified in assignment.
		if ((rentDate.getNameOfDay().contentEquals("Sunday") || rentDate.getNameOfDay().contentEquals("Monday")
				|| rentDate.getNameOfDay().contentEquals("Tuesday")
				|| rentDate.getNameOfDay().contentEquals("Wednesday")
				|| rentDate.getNameOfDay().contentEquals("Thursday")) && numOfDays < 2) {
			throw new RentException("Car should be rented for 2 or more days if renting between Sunday-Thursday");
		}

		if ((rentDate.getNameOfDay().contentEquals("Friday") || rentDate.getNameOfDay().contentEquals("Saturday"))
				&& numOfDays < 3) {

			throw new RentException("Car should be rented for 3 or more days if renting on Friday or Saturday");
		}
		
		if (numOfDays > 14) {
			throw new RentException("Car can be rented for a maximum of 14 days");
		}

	}

	@Override
	public float getRentalRate() {
		
		// Return per day rental rate for specific vehicle based on seat count
		if (this.getModel().getNoOfSeats() == 4) {
			return SEAT_4_RENTAL;
		} else {
			return SEAT_7_RENTAL;
		}
	}

	@Override
	public float getLateRate() {
		// Return per day late rate
		return LATE_PERCENT * getRentalRate();
	}


}
