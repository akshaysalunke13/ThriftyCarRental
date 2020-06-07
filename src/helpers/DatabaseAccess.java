package helpers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Car;
import model.Van;
import model.Vehicle;
import model.RentalRecord;
import exception.AddVehicleException;
import helpers.DatabaseAccess;
import helpers.DateTime;

public class DatabaseAccess {

	private static final String DB_NAME = "ThriftyCarRental";

	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbc.JDBCDriver");

		Connection con = DriverManager.getConnection("jdbc:hsqldb:file:database/" + DB_NAME, "SA", "");

		return con;
	}

	public static void createTables() {
		if (!checkTablesExist("vehicles")) {
			createVehicleTable();
		}
		if (!checkTablesExist("rentalrecord")) {
			createRentalRecordTable();
		}
	}

	private static boolean checkTablesExist(String tableName) {
		try (Connection con = getConnection()) {
			DatabaseMetaData dbm = con.getMetaData();
			ResultSet tables = dbm.getTables(null, null, tableName.toUpperCase(), null);

			if (tables != null) {
				if (tables.next()) {
					System.out.println("Table " + tableName + " exists.");
					tables.close();
					return true;
				} else {
					tables.close();
					return false;
				}
			} else {
				System.out.println(("Problem with retrieving database metadata."));
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}

	}

	private static void createVehicleTable() {
		try (Connection con = DatabaseAccess.getConnection(); Statement stmt = con.createStatement();) {

			int result = stmt.executeUpdate("CREATE TABLE vehicles (" + "id VARCHAR(30) NOT NULL,"
					+ "mfd_year INT NOT NULL," + "make VARCHAR(20) NOT NULL," + "model VARCHAR(20) NOT NULL,"
					+ "num_of_seats INT NOT NULL," + "status VARCHAR(20)," + "last_maintenance_date DATE,"
					+ "image_name VARCHAR(50)," + "PRIMARY KEY (id))");

			if (result == 0) {
				System.out.println("Table VEHICLES created successfully.");
			} else {
				System.out.println("Table VEHICLES is not created.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static void createRentalRecordTable() {
		try (Connection con = DatabaseAccess.getConnection(); Statement stmt = con.createStatement();) {

			int result = stmt.executeUpdate("CREATE TABLE rentalrecord (" + "id VARCHAR(50) NOT NULL,"
					+ "cust_id VARCHAR(30) NOT NULL," + "vehicle_id VARCHAR(20) NOT NULL," + "rent_date DATE NOT NULL,"
					+ "expected_return_date DATE NOT NULL," + "actual_return_date DATE," + "rent_fee NUMERIC,"
					+ "late_fee NUMERIC," + "PRIMARY KEY (id)," + "FOREIGN KEY (vehicle_id) REFERENCES vehicles(id))");
			if (result == 0) {
				System.out.println("Table RENTALRECORD has been created successfully.");
			} else {
				System.out.println("Table RENTALRECORD is not created.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	//Insert vehicle into database
	public static void insertVehicle(Vehicle vehicle) throws SQLException {
		try (Connection con = DatabaseAccess.getConnection();
				PreparedStatement stmt = con
						.prepareStatement("INSERT INTO VEHICLES VALUES (?, ?, ?, ?, ?, ?, ?, ?)");) {

			stmt.setString(1, vehicle.getVehicleID());
			stmt.setString(2, Integer.toString(vehicle.getMfdYear()));
			stmt.setString(3, vehicle.getMake());
			stmt.setString(4, vehicle.getModel());
			stmt.setInt(5, vehicle.getNoOfSeats());
			stmt.setString(6, vehicle.getStatus());

			Date lastMaintenanceDate = null;
			if (vehicle.getLastMaintenanceDate() != null) {
				lastMaintenanceDate = new Date(vehicle.getLastMaintenanceDate().getTime());
			}
			stmt.setDate(7, lastMaintenanceDate);
			stmt.setString(8, vehicle.getImgName());

			stmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	//Insert rental record row into database
	public static void insertRentalRecordRow(RentalRecord record) throws SQLException {
		try (Connection con = DatabaseAccess.getConnection();
				PreparedStatement stmt = con
						.prepareStatement("INSERT INTO RENTALRECORD VALUES (?, ?, ?, ?, ?, ?, ?, ?)");) {

			stmt.setString(1, record.getRecordID());
			stmt.setString(2, record.getCustID());
			stmt.setString(3, record.getVehicleID());
			Date rentDate = new Date(record.getRentDate().getTime());
			stmt.setDate(4, rentDate);
			Date expReturnDate = new Date(record.getExpectedReturnDate().getTime());
			stmt.setDate(5, expReturnDate);
			Date actualReturnDate = null;
			if (record.getActualReturnDate() != null) {
				actualReturnDate = new Date(record.getActualReturnDate().getTime());
			}
			stmt.setDate(6, actualReturnDate);
			stmt.setDouble(7, record.getRentFee());
			stmt.setDouble(8, record.getLateFee());

			stmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	//Update vehicle properties in vehicles db
	public static void updateVehicle(Vehicle vehicle) throws SQLException {

		try (Connection con = DatabaseAccess.getConnection();
				PreparedStatement stmt = con
						.prepareStatement("UPDATE VEHICLES SET status = ?, last_maintenance_date = ? WHERE id = ?");) {

			stmt.setString(1, vehicle.getStatus());
			Date lastMaintenanceDate = null;

			if (vehicle.getLastMaintenanceDate() != null) {
				lastMaintenanceDate = new Date(vehicle.getLastMaintenanceDate().getTime());
			}

			stmt.setDate(2, lastMaintenanceDate);
			stmt.setString(3, vehicle.getVehicleID());
			stmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	//Update rental record row in database
	public static void updateRentalRecordRow(RentalRecord record) throws SQLException {

		try (Connection con = DatabaseAccess.getConnection();
				PreparedStatement stmt = con.prepareStatement(
						"UPDATE RENTALRECORD SET actual_return_date = ?, rent_fee = ?, late_fee = ? WHERE id = ?");) {

			Date actReturnDate = null;

			if (record.getActualReturnDate() != null) {
				actReturnDate = new Date(record.getActualReturnDate().getTime());
			}

			stmt.setDate(1, actReturnDate);
			stmt.setDouble(2, record.getRentFee());
			stmt.setDouble(3, record.getLateFee());
			stmt.setString(4, record.getRecordID());
			stmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Check if vehicle with parameter id exists in database
	public static void checkVehicleExists(String id) throws AddVehicleException {
		try (Connection con = DatabaseAccess.getConnection();
				PreparedStatement stmt = con.prepareStatement("SELECT * FROM VEHICLES WHERE id = ?");) {

			stmt.setString(1, id);
			ResultSet result = stmt.executeQuery();

			if (result.next()) {
				throw new AddVehicleException("Vehicle ID already exists.");
			}

		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	// Get all vehicles from database
	public static List<Vehicle> getAllVehicles() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>();

		try (Connection con = DatabaseAccess.getConnection(); Statement stmt = con.createStatement();) {
			String query = "SELECT * FROM VEHICLES";

			try (ResultSet resultSet = stmt.executeQuery(query)) {

				while (resultSet.next()) {
					vehicles.add(transformRowToVehicle(resultSet));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vehicles;
	}

	// Get all rental records for a specific vehicle id
	public static List<RentalRecord> getAllRecordsForID(String vehicleID) {

		List<RentalRecord> records = new ArrayList<RentalRecord>();

		try (Connection con = DatabaseAccess.getConnection();
				PreparedStatement stmt = con.prepareStatement(
						"SELECT * FROM RENTALRECORD WHERE vehicle_id = ? ORDER BY CASE actual_return_date WHEN IS NULL THEN 0 ELSE 1 END, rent_date desc");) {

			stmt.setString(1, vehicleID);

			try (ResultSet resultSet = stmt.executeQuery()) {
				while (resultSet.next()) {
					records.add(transformRowToRecord(resultSet));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return records;
	}

	// Convert record row from database to Rental Record object
	private static RentalRecord transformRowToRecord(ResultSet row) throws SQLException {

		String vehicleID = row.getString("VEHICLE_ID");
		String customerID = row.getString("CUST_ID");

		String temp = row.getString("RENT_DATE");
		DateTime rentDate = parseSqlDate(temp);

		temp = row.getString("EXPECTED_RETURN_DATE");
		DateTime estReturnDate = parseSqlDate(temp);

		RentalRecord record = new RentalRecord(vehicleID, customerID, rentDate, estReturnDate);
		if (row.getDate("ACTUAL_RETURN_DATE") != null) {

			temp = row.getString("ACTUAL_RETURN_DATE");

			DateTime actReturnDate = parseSqlDate(temp);

			record.setActualReturnDate(actReturnDate);
		}

		double rentalFee = row.getDouble("RENT_FEE");

		record.setRentFee((float) rentalFee);

		double lateFee = row.getDouble("LATE_FEE");

		record.setLateFee((float) lateFee);
		return record;
	}

	//Convert record row from database to Vehicle object
	private static Vehicle transformRowToVehicle(ResultSet row) throws SQLException {

		Vehicle vehicle;
		String id = row.getString("ID");
		int mfd = row.getInt("MFD_YEAR");

		String make = row.getString("MAKE");
		String model = row.getString("MODEL");
		int seats = row.getInt("NUM_OF_SEATS");
		String status = row.getString("STATUS");

		// String type = row.getString("TYPE");
		String t = row.getString("LAST_MAINTENANCE_DATE");

		if (row.wasNull()) {

			vehicle = new Car(id, mfd, make, model, seats);

		} else {

			DateTime lastMaintenanceDate = parseSqlDate(t);
			
			vehicle = new Van(id, mfd, make, model, lastMaintenanceDate);
		}

		vehicle.setStatus(status);
		String imageName = row.getString("IMAGE_NAME");
		vehicle.setImgName(imageName);

		return vehicle;
	}

	// Function to parse date read from database to DateTime object. Input in form of dd-mm-yyyy
	public static DateTime parseSqlDate(String t) {
		String[] s = t.split("-");
		int yy = Integer.parseInt(s[0]);
		int mm = Integer.parseInt(s[1]);
		int dd = Integer.parseInt(s[2]);
		return new DateTime(dd, mm, yy);
	}

	//Function to get latest record of vehicle id.
	public static RentalRecord getLatestRentalRecordForID(String vehicleID) {

		RentalRecord record = null;
		try (Connection con = DatabaseAccess.getConnection();

				PreparedStatement stmt = con.prepareStatement(
						"SELECT * FROM RENTALRECORD WHERE vehicle_id = ? AND actual_return_date IS NULL");) {

			stmt.setString(1, vehicleID);

			try (ResultSet resultSet = stmt.executeQuery()) {

				while (resultSet.next()) {
					record = transformRowToRecord(resultSet);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return record;
	}
}
