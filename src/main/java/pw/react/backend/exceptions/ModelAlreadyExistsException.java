package pw.react.backend.exceptions;

public class ModelAlreadyExistsException extends RuntimeException {
    public ModelAlreadyExistsException(String message) {
        super(message);
    }
}
