package coffeeshop.util;

import coffeeshop.exception.ValidationException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Helper methods for checking user input. */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern NUMBER = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9 ]{7,15}$");
    public static final int MAX_TEXT_LENGTH = 100;

    private Validator() {
    // utility class, no instances
    }

    /** Checks that the text is not empty and not too long; returns the trimmed text. */
    public static String requireText(String value, String fieldName) throws ValidationException {
        return requireText(value, fieldName, MAX_TEXT_LENGTH);
    }

    /** Overloaded version with custom maximum length. */
    public static String requireText(String value, String fieldName, int maxLength) throws ValidationException {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            throw new ValidationException("Field \"" + fieldName + "\" is required.");
        }
        if (text.length() > maxLength) {
            throw new ValidationException("Field \"" + fieldName + "\" must be at most " + maxLength + " characters.");
        }
        return text;
    }

    /** Parses a decimal number greater than 0 (comma is accepted as decimal separator). */
    public static double parsePositiveDouble(String value, String fieldName) throws ValidationException {
        return parseDouble(value, fieldName, 0.01, 100000);
    }

    /** Parses a decimal number in the range [min, max]. */
    public static double parseDouble(String value, String fieldName, double min, double max) throws ValidationException {
        String text = requireText(value, fieldName).replace(',', '.');
        // Double.parseDouble() also accepts "1e2", "12.5d" or "0x10p0", so the format is checked first
        if (!NUMBER.matcher(text).matches()) {
            throw new ValidationException("Field \"" + fieldName + "\" must be a number, e.g. 12.50");
        }
        double number = Double.parseDouble(text);
        if (number < min || number > max) {
            throw new ValidationException("Field \"" + fieldName + "\" must be between "
                    + plain(min) + " and " + plain(max) + ".");
        }
        return number;
    }

    /** Writes a number without unnecessary zeros: 100000.0 -> "100000". */
    private static String plain(double number) {
        return new BigDecimal(String.valueOf(number)).stripTrailingZeros().toPlainString();
    }

    /** Parses a whole number in the range [min, max]. */
    public static int parseInt(String value, String fieldName, int min, int max) throws ValidationException {
        String text = requireText(value, fieldName);
        int number;
        try {
            number = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new ValidationException("Field \"" + fieldName + "\" must be a whole number.");
        }
        if (number < min || number > max) {
            throw new ValidationException("Field \"" + fieldName + "\" must be between " + min + " and " + max + ".");
        }
        return number;
    }

    /** Checks e-mail format, e.g. name@mail.com */
    public static String requireEmail(String value) throws ValidationException {
        String email = requireText(value, "E-mail");
        if (!EMAIL.matcher(email).matches()) {
            throw new ValidationException("E-mail must look like name@example.com");
        }
        return email;
    }

    /** Checks phone format: optional "+", then 7-15 digits or spaces. Empty phone is allowed. */
    public static String checkPhone(String value) throws ValidationException {
        String phone = value == null ? "" : value.trim();
        if (!phone.isEmpty() && !PHONE.matcher(phone).matches()) {
            throw new ValidationException("Phone must contain 7-15 digits, e.g. +371 20000000");
        }
        return phone;
    }

    /** Checks that an object (e.g. a combo box selection) is chosen. */
    public static <T> T requireSelected(T value, String fieldName) throws ValidationException {
        if (value == null) {
            throw new ValidationException("Please select \"" + fieldName + "\".");
        }
        return value;
    }
}
