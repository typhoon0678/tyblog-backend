package store.tyblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import store.tyblog.common.exception.custom.UserAlreadyExistsException;
import store.tyblog.dto.member.MemberSignupRequestDto;
import store.tyblog.dto.member.MemberTokenResponseDto;
import store.tyblog.entity.Member;
import store.tyblog.enums.Platform;
import store.tyblog.enums.Role;
import store.tyblog.repository.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(MemberSignupRequestDto memberSignupRequestDto) {

        if (memberRepository.findByEmail(memberSignupRequestDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("이미 가입되어 있는 유저입니다.");
        }

        String username = memberSignupRequestDto.getEmail().split("@")[0]
                + "_" + UUID.randomUUID().toString().substring(0, 4);

        Member signupMember = Member.builder()
                .email(memberSignupRequestDto.getEmail())
                .password(passwordEncoder.encode(memberSignupRequestDto.getPassword()))
                .username(username)
                .role(Role.ROLE_USER)
                .platform(Platform.SERVER)
                .build();

        memberRepository.save(signupMember);
    }

    public MemberTokenResponseDto getMemberInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            return MemberTokenResponseDto.builder()
                    .email(email)
                    .roles(roles)
                    .build();
        }

        return new MemberTokenResponseDto();
    }

    public MemberTokenResponseDto getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            String username = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("회원 정보가 없습니다."))
                    .getUsername();

            return MemberTokenResponseDto.builder()
                    .username(username)
                    .build();
        }

        return new MemberTokenResponseDto();
    }
}
