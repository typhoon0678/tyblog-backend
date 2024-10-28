package store.tyblog.common.exception.custom;

public class UserAlreadyExistsException extends IllegalArgumentException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
