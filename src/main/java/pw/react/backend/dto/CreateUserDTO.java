package pw.react.backend.dto;

import jakarta.validation.constraints.Email;
import pw.react.backend.models.User;
import pw.react.backend.utils.UserRole;

public record CreateUserDTO(String username, @Email String email, String firstName, String lastName, UserRole role) {
    public static CreateUserDTO fromModel(User user) {
        return new CreateUserDTO(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
    }

    public User toModel() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        return user;
    }
}
