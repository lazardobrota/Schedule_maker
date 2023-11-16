package exceptions;

public class InvalidRoomException extends Exception{

    /**
     * Exception if they room is invalid
     * @param message
     */
    public InvalidRoomException(String message) {
        super(message);
    }
}
