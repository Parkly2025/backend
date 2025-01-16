package pw.react.backend.utils;

import java.util.List;

public enum UserRole {
    ADMIN(List.of("ADMIN", "admin")),
    USER(List.of("USER", "user")),
    GUEST(List.of("GUEST", "guest"));

    private final List<String> authorities;

    UserRole(List<String> authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities(){
        return authorities.toArray(new String[0]);
    }
}
