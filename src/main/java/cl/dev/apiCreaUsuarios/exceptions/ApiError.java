package cl.dev.apiCreaUsuarios.exceptions;

import org.springframework.http.HttpStatus;

public class ApiError extends RuntimeException {

    private final HttpStatus status;

    public ApiError(String message, HttpStatus status) {
        super(message); // Usa el constructor de RuntimeException
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
