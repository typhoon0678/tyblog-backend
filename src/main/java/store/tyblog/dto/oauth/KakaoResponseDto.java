package store.tyblog.dto.oauth;

import store.tyblog.enums.Platform;

import java.util.Map;

public class KakaoResponseDto implements OAuth2ResponseDto {

    private final Map<String, Object> attribute;

    public KakaoResponseDto(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("kakao_account");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> profile = (Map<String, Object>) attribute.get("profile");
        return profile.get("nickname").toString();
    }

    @Override
    public Platform getPlatform() {
        return Platform.KAKAO;
    }
}
