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
    private final long tokenValidityInSeconds;
    private final long tokenValidityInMilliseconds;
    private SecretKey secretKey;
    private final String domain;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            @Value("${jwt.domain}") String domain) {
        this.secret = secret;
        this.tokenValidityInSeconds = tokenValidityInSeconds;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.domain = domain;
    }

    @Override
    public void afterPropertiesSet() {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    public ResponseCookie getAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        String jwt = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(secretKey)
                .expiration(validity)
                .compact();

        return ResponseCookie.from("accessToken")
                .value(jwt)
                .domain(domain)
                .path("/")
                .httpOnly(true)
//                .secure(true) // https 에서만 쿠키 전송
                .maxAge(tokenValidityInSeconds)
                .build();
    }

    public Map<String, ResponseCookie> getLogoutToken() {
        Map<String, ResponseCookie> resultMap = new HashMap<>();

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken")
                .value("")
                .domain(domain)
                .path("/")
                .httpOnly(true)
//                .secure(true) // https 에서만 쿠키 전송
                .maxAge(0)
                .build();

        ResponseCookie loginCookie =
                ResponseCookie.from("login")
                        .value(UUID.randomUUID().toString())
                        .domain(domain)
                        .path("/")
                        .maxAge(0)
                        .build();

        resultMap.put("accessToken", accessTokenCookie);
        resultMap.put("login", loginCookie);

        return resultMap;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
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

    public Cookie getAccessServletCookie(Authentication authentication) {

        return toServletCookie(getAccessToken(authentication));
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