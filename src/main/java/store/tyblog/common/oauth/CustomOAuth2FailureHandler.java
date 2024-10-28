package store.tyblog.common.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import store.tyblog.common.exception.custom.OAuth2UserAlreadyException;

import java.io.IOException;

@Component
public class CustomOAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${client.url}")
    String clientUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        OAuth2UserAlreadyException oAuth2UserAlreadyException = (OAuth2UserAlreadyException) exception;

        response.sendRedirect(clientUrl + "/login?error=" + oAuth2UserAlreadyException.getError().getErrorCode());
    }
}
