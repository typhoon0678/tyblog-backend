package store.tyblog.common.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import store.tyblog.dto.oauth.MemberOAuth2Dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final MemberOAuth2Dto memberOAuth2Dto;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(memberOAuth2Dto.getRole().getRole()));
    }

    @Override
    public String getName() {
        return memberOAuth2Dto.getEmail();
    }

    public String getUsername() {
        return memberOAuth2Dto.getUsername();
    }
}
