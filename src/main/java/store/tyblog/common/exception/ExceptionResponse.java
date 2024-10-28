package store.tyblog.common.exception;

import lombok.Data;

@Data
public class ExceptionResponse<T> {

    private String message;

    private T data;

    public ExceptionResponse(String message) {
        this.message = message;
    }

    public ExceptionResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
