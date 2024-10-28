package store.tyblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import store.tyblog.dto.oauth.*;
import store.tyblog.entity.Member;
import store.tyblog.enums.Role;
import store.tyblog.common.exception.custom.OAuth2UserAlreadyException;
import store.tyblog.common.oauth.CustomOAuth2User;
import store.tyblog.repository.MemberRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2ResponseDto oAuth2ResponseDto;

        switch (registrationId) {
            case "naver" -> oAuth2ResponseDto = new NaverResponseDto(oAuth2User.getAttributes());
            case "google" -> oAuth2ResponseDto = new GoogleResponseDto(oAuth2User.getAttributes());
            case "kakao" -> oAuth2ResponseDto = new KakaoResponseDto(oAuth2User.getAttributes());
            default -> {
                return null;
            }
        }

        Optional<Member> optionalMember = memberRepository.findByEmail(oAuth2ResponseDto.getEmail());
        Member member;
        if (optionalMember.isEmpty()) {

            String username = memberRepository.findByUsername(oAuth2ResponseDto.getName()).isEmpty()
                    ? oAuth2ResponseDto.getName() : oAuth2ResponseDto.getName() + "_" + UUID.randomUUID().toString().substring(0, 4);

            // 회원 정보가 없는 경우 저장
            member = Member.builder()
                    .email(oAuth2ResponseDto.getEmail())
                    .username(username)
                    .password(UUID.randomUUID().toString())
                    .role(Role.ROLE_USER)
                    .platform(oAuth2ResponseDto.getPlatform())
                    .build();

            memberRepository.save(member);
        } else {

            member = optionalMember.get();

            if (!member.getPlatform().equals(oAuth2ResponseDto.getPlatform())) {

                // 플랫폼이 같지 않은 경우 -> 중복 회원가입을 막기 위해 throw Exception
                throw new OAuth2UserAlreadyException(member.getPlatform().getPlatform());
            }

            memberRepository.save(member);
        }

        MemberOAuth2Dto memberOAuth2Dto = MemberOAuth2Dto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .role(member.getRole())
                .build();

        return new CustomOAuth2User(memberOAuth2Dto);
    }
}
