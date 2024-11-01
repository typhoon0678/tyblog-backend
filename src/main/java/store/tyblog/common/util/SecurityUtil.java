package store.tyblog.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.NoSuchElementException;

public class SecurityUtil {

    public static String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new NoSuchElementException("로그인 정보가 없습니다.");
        }

        return authentication.getName();
    }
}
