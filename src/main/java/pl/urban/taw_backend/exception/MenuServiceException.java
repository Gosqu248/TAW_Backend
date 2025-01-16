package pl.urban.taw_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MenuServiceException extends RuntimeException {
    public MenuServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
