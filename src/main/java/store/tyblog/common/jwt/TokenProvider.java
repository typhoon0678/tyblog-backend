package store.tyblog.common.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long accessValidityInMilliseconds;
    private final long refreshValidityInSeconds;
    private final long refreshValidityInMilliseconds;
    private SecretKey secretKey;
    private final String domain;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expired.access}") long accessValidityInSeconds,
            @Value("${jwt.expired.refresh}") long refreshValidityInSeconds,
            @Value("${jwt.domain}") String domain) {
        this.secret = secret;
        this.accessValidityInMilliseconds = accessValidityInSeconds * 1000;
        this.refreshValidityInSeconds = refreshValidityInSeconds;
        this.refreshValidityInMilliseconds = refreshValidityInSeconds * 1000;
        this.domain = domain;
    }

    @Override
    public void afterPropertiesSet() {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    public String getJwt(String category, Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        long tokenValidityInMilliseconds = 0L;
        if (category.equals("access")) {
            tokenValidityInMilliseconds = this.accessValidityInMilliseconds;
        } else if (category.equals("refresh")) {
            tokenValidityInMilliseconds = this.refreshValidityInMilliseconds;
        }
        Date validity = new Date(now + tokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("category", category)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(secretKey)
                .expiration(validity)
                .compact();
    }

    public ResponseCookie getRefreshToken(Authentication authentication) {

        String jwt = getJwt("refresh", authentication);

        return ResponseCookie.from("refresh")
                .value(jwt)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                // .secure(true) // https 에서만 쿠키 전송
                .maxAge(refreshValidityInSeconds)
                .build();
    }

    public Map<String, ResponseCookie> getLogoutToken() {
        Map<String, ResponseCookie> resultMap = new HashMap<>();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh")
                .value("")
                .domain(domain)
                .path("/")
                .httpOnly(true)
                // .secure(true) // https 에서만 쿠키 전송
                .maxAge(0)
                .build();

        resultMap.put("refresh", refreshTokenCookie);

        return resultMap;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ignored) {
        }
        return false;
    }

    public Cookie getRefreshServletCookie(Authentication authentication) {

        return toServletCookie(getRefreshToken(authentication));
    }

    private Cookie toServletCookie(ResponseCookie responseCookie) {

        Cookie cookie = new Cookie(responseCookie.getName(), responseCookie.getValue());
        cookie.setDomain(domain);
        cookie.setMaxAge((int) responseCookie.getMaxAge().getSeconds());
        cookie.setPath("/");
        cookie.setHttpOnly(responseCookie.isHttpOnly());
        cookie.setSecure(responseCookie.isSecure());
        return cookie;
    }
}