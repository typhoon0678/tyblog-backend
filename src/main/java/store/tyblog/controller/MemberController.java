package store.tyblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import store.tyblog.common.jwt.TokenProvider;
import store.tyblog.dto.member.MemberLoginRequestDto;
import store.tyblog.dto.member.MemberSignupRequestDto;
import store.tyblog.dto.member.MemberTokenResponseDto;
import store.tyblog.service.MemberService;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberLoginRequestDto.getEmail(), memberLoginRequestDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseCookie accessToken = tokenProvider.getAccessToken(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessToken.toString())
                .build();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        Map logoutCookieMap = tokenProvider.getLogoutToken();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, logoutCookieMap.get("accessToken").toString())
                .build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> saveMember(@Valid @RequestBody MemberSignupRequestDto memberSignupRequestDto) {
        memberService.signup(memberSignupRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token")
    @Operation(summary = "토큰 확인 API", description = "API 요청에 포함된 토큰이 유효한지 확인 후, 회원 정보를 return")
    public ResponseEntity<MemberTokenResponseDto> checkToken() {
        MemberTokenResponseDto memberTokenResponseDto = memberService.getMemberInfo();

        return ResponseEntity.ok().body(memberTokenResponseDto);
    }

    @GetMapping("/username")
    public ResponseEntity<MemberTokenResponseDto> getUsername() {
        MemberTokenResponseDto memberTokenResponseDto = memberService.getUsername();

        return ResponseEntity.ok().body(memberTokenResponseDto);
    }
}
