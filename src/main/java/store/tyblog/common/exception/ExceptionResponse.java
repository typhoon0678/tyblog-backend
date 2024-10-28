package store.tyblog.common.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponse<T> {

    private String code;

    private String message;

    private T data;
}
