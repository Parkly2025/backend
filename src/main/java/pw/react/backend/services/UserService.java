package pw.react.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pw.react.backend.dto.CreateUserDTO;
import pw.react.backend.models.User;

import java.util.Collection;
import java.util.Optional;

@Service
public interface UserService {
    Page<User> findAll(int page, int size, String sortDirection, String searchQuery, String searchQueryParameter);
    Optional<User> findById(Long id);
    User create(CreateUserDTO userDTO);
    Optional<User> update(Long id, User updatedUser);
    void delete(Long id);
}
