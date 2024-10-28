package store.tyblog.dto.oauth;

import lombok.Builder;
import lombok.Data;
import store.tyblog.enums.Role;

@Data
@Builder
public class MemberOAuth2Dto {

    private String email;
    private String username;
    private Role role;
}
