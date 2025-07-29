package pe.edu.vallegrande.vg_ms_user.infrastructure.exception;

public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String message) {
        super(message);
    }
}