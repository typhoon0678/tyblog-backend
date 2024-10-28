package store.tyblog.dto.member;

import lombok.Data;

@Data
public class MemberSignupRequestDto {

    private String email;
    private String password;
}
