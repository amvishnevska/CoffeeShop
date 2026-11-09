package coffeeshop.exception;

/** Thrown when data cannot be saved to or loaded from the file. */
public class StorageException extends Exception {

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
