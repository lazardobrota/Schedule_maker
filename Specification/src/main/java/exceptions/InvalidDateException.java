package exceptions;

public class InvalidDateException extends Exception{

    /**
     * Exception if they date is invalid
     * @param message
     */
    public InvalidDateException(String message) {
        super(message);
    }
}
