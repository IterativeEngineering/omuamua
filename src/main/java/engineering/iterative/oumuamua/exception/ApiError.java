package engineering.iterative.oumuamua.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A simple POJO for representing API errors in responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;
}
