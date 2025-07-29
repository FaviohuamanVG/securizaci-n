package pe.edu.vallegrande.vg_ms_user.infrastructure.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}