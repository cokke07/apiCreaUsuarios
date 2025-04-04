package cl.dev.apiCreaUsuarios.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiError.class)
    public ResponseEntity<Map<String, String>> handleApiError(ApiError ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(errorBody, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpectedError(Exception ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("mensaje", "Error interno del servidor");
        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
