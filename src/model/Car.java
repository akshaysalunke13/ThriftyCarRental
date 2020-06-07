package model;

public class Car extends Vehicle {

	// Public constructor to create new Car object
	public Car(String id, int mfd_year, String make, String model, int seat_capacity) {
		
		//Every vehicle is Available when it's initialized
		super(id, mfd_year, make, model, seat_capacity, "Available");
	}

}