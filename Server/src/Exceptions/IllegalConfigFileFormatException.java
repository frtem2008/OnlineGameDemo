package Exceptions;

public class IllegalConfigFileFormatException extends Exception {
    public IllegalConfigFileFormatException(String message) {
        super(message);
    }

    public IllegalConfigFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalConfigFileFormatException(Throwable cause) {
        super(cause);
    }
}
