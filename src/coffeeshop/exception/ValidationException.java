package coffeeshop.exception;

/** Thrown when the data entered by the user is not valid. */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
