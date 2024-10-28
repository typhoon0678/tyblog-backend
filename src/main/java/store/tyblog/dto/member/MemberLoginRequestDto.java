package store.tyblog.dto.member;

import lombok.Data;

@Data
public class MemberLoginRequestDto {

    private String email;
    private String password;
}
