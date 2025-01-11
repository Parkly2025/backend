package pw.react.backend.exceptions;

public class ModelValidationException extends RuntimeException {
    public ModelValidationException(String message) {
        super(message);
    }
}
