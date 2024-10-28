package store.tyblog.common.exception.custom;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2UserAlreadyException extends OAuth2AuthenticationException {

    public OAuth2UserAlreadyException(String message) {
        super(message);
    }
}
