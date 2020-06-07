package controller;

import java.sql.SQLException;

import exception.RentException;
import exception.ReturnException;
import helpers.DateTime;

public interface Rentable {

	public void rent(String customerID, DateTime rentDate, int numOfRentDay) throws RentException, SQLException;

	public void returnVehicle(DateTime returnDate) throws ReturnException, SQLException;
}
