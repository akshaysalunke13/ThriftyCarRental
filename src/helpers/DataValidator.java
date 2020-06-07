package helpers;

import exception.InputException;

public class DataValidator {

	// Validate number
	public static void validateNumber(String string) throws InputException {
		if (!string.matches("[0-9]+")) {
			throw new InputException("The input should be a whole number.");
		}
	}

	// Validate text
	public static void validateText(String string) throws InputException {
		if (!string.matches("[A-Za-z ]+")) {
			throw new InputException("The input should contain letters only.");
		}
	}

	// Validate Alpha numeric
	public static void validateAlphaNum(String string) throws InputException {
		if (!string.matches("[A-Za-z_1-9]+")) {
			throw new InputException("The input should contain letters and numbers only.");
		}
	}

	// Validate length of string
	public static void validateLength(String string) throws InputException {
		if (string.length() > 100) {
			throw new InputException("The input must be fewer than 100 characters.");
		}
	}

	// Validate vehicle id format
	public static void validateVehicleID(String text) throws InputException {
		// TODO Auto-generated method stub

		if (!(text.startsWith("C_") || text.startsWith("V_"))) {
			throw new InputException("Vehicle id should start with C_ for Car and V_ for Van");
		}
	}
}
