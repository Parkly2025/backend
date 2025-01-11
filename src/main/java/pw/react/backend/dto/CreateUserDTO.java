package pw.react.backend.dto;

import jakarta.validation.constraints.Email;
import pw.react.backend.models.User;

public record CreateUserDTO(String username, @Email String email, String firstName, String lastName) {
    public static CreateUserDTO fromModel(User user) {
        return new CreateUserDTO(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());
    }

    public User toModel() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}
