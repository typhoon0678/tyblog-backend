package store.tyblog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_SELLER("ROLE_SELLER"),
    ROLE_USER("ROLE_USER");

    private final String role;
}
