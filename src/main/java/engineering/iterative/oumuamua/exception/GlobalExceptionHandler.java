package engineering.iterative.oumuamua.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler that intercepts exceptions and provides structured error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles all exceptions and looks for specific error message patterns to provide
     * appropriate responses.
     *
     * @param ex The exception that was thrown
     * @return ResponseEntity with appropriate error message and status code
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred";

        // Check for read-only transaction error
        if (ex.getMessage() != null && 
            ex.getMessage().contains("ERROR: cannot execute INSERT in a read-only transaction")) {
            status = HttpStatus.BAD_REQUEST;
            message = "Cannot execute insert on replica";
        }

        response.put("message", message);
        response.put("status", status.value());

        return new ResponseEntity<>(response, status);
    }
}
