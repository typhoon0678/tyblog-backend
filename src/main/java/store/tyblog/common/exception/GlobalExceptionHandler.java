package store.tyblog.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.tyblog.common.exception.custom.OAuth2UserAlreadyException;
import store.tyblog.common.exception.custom.UserAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        ExceptionResponse<?> exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(OAuth2UserAlreadyException.class)
    protected ResponseEntity<Object> handleUserAlreadyException(OAuth2UserAlreadyException e) {
        ExceptionResponse<?> exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }
}
