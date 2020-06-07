package helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import model.Car;
import model.Van;
import model.Vehicle;
import model.RentalRecord;
import exception.AddVehicleException;
import exception.ExportException;

public class FileAccess {

	// Function to import data from text file
	public static void importFile(File file) throws SQLException, AddVehicleException {

		try (Scanner input = new Scanner(file)) {

			while (input.hasNextLine()) {
				importLine(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Function to process current read line from file 
	private static void importLine(String line) throws SQLException, AddVehicleException {
		String[] elements = line.split(":");
		int len = elements.length;

		if (len == 7 || len == 8) {
			// this is a car or van
			insertVehicle(elements);
		} else if (len == 6) {
			// this is a record
			insertRecord(elements);
		}
	}

	// Function to create Vehicle object from read line
	private static void insertVehicle(String[] elements) throws SQLException, AddVehicleException {

		Vehicle newVehicle;

		if (elements[0].startsWith("C_")) {
			// Process car
			newVehicle = new Car(elements[0], Integer.parseInt(elements[1]), elements[2], elements[3],
					Integer.parseInt(elements[4]));

			newVehicle.setStatus(elements[5]);
			newVehicle.setImgName(elements[6]);

		} else if (elements[0].startsWith("V_")) {
			// Process Van
			DateTime maintainanceDate = parseDate(elements[6]);

			newVehicle = new Van(elements[0], Integer.parseInt(elements[1]), elements[2], elements[3],
					maintainanceDate);
			newVehicle.setStatus(elements[5]);
			newVehicle.setImgName(elements[7]);

		} else {
			throw new AddVehicleException("Invalid format for vehicle details.");
		}

		DatabaseAccess.insertVehicle(newVehicle);
	}

	// Parse date from string
	public static DateTime parseDate(String s) {

		// parse date from dd/mm/yyy to DateTime object
		String[] dateString = s.split("/");
		int dd = Integer.parseInt(dateString[0]);
		int mm = Integer.parseInt(dateString[1]);
		int yy = Integer.parseInt(dateString[2]);

		DateTime date = new DateTime(dd, mm, yy);

		return date;
	}

	// export rental record to text file
	private static void insertRecord(String[] elements) throws SQLException {
		// Record constructor
		RentalRecord newRecord;
		String id = elements[0];

		String[] idComponents = id.split("_");

		//Extract vehicle id from record id
		String vehicleID = idComponents[0] + "_" + idComponents[1];

		// Extract Customer id from record id
		String customerID = id.replaceAll(vehicleID + "_", "").replaceAll("_" + idComponents[idComponents.length - 1],
				"");

		DateTime actReturnDate = elements[3].equals("none") ? null : parseDate(elements[3]);
		float rentalFee = elements[4].equals("none") ? 0.0f : Float.parseFloat((elements[4]));
		float lateFee = elements[5].equals("none") ? 0.0f : Float.parseFloat(elements[5]);

		newRecord = new RentalRecord(vehicleID, customerID, parseDate(elements[1]), parseDate(elements[2]));
		newRecord.setActualReturnDate(actReturnDate);
		newRecord.setRentFee(rentalFee);
		newRecord.setLateFee(lateFee);

		// Add this record to database
		DatabaseAccess.insertRentalRecordRow(newRecord);
	}

	// Export all data to text file
	public static void exportAll(File directory) throws ExportException, FileNotFoundException {

		List<Vehicle> vehicles = DatabaseAccess.getAllVehicles();

		if (vehicles.size() == 0) {
			throw new ExportException("Database empty!");
		}

		File file = new File(directory.getAbsolutePath() + "/export_data.txt");

		PrintWriter exporter = new PrintWriter(file);

		Iterator<Vehicle> it = vehicles.iterator();

		while (it.hasNext()) {
			Vehicle vehicle = it.next();

			exporter.write(vehicle.toString());

			exporter.println();
			List<RentalRecord> records = DatabaseAccess.getAllRecordsForID(vehicle.getVehicleID());
			if (records.size() == 0) {
				continue;
			}

			Iterator<RentalRecord> i = records.iterator();

			for (int x = 0; x < 3 && i.hasNext(); x++) {

				RentalRecord record = i.next();
				exporter.write(record.toString());
				exporter.println();
			}
			exporter.println();
		}
		exporter.close();
	}
}
