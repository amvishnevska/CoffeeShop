package coffeeshop.exception;

/** Thrown when an operation breaks a rule of the shop, e.g. not enough items in stock. */
public class BusinessException extends Exception {

    public BusinessException(String message) {
        super(message);
    }
}
