package cl.dev.apiCreaUsuarios.exceptions;

import org.springframework.http.HttpStatus;

public class ApiError extends Throwable {
    private HttpStatus status;
    private String message;

    public ApiError(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}