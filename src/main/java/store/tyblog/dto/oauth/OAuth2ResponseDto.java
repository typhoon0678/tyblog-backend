package store.tyblog.dto.oauth;

import store.tyblog.enums.Platform;

public interface OAuth2ResponseDto {

    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();

    Platform getPlatform();
}
