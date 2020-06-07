package controller;

import java.sql.SQLException;

import helpers.DateTime;

public interface Maintainable {

	public void performMaintenance() throws SQLException;

	public void completeMaintenance(DateTime completeDate) throws SQLException;
}
