package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.dto.CreateUserDTO;
import pw.react.backend.exceptions.ModelAlreadyExistsException;
import pw.react.backend.exceptions.ModelNotFoundException;
import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.User;
import pw.react.backend.specifications.UserSpecification;

import java.util.*;
import java.util.function.Predicate;

@Service
public class UserMainService implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserMainService.class);

    protected final UserRepository userRepository;

    public UserMainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private boolean isInvalid(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public Page<User> findAll(int page, int size, String sortDirection,
                              String searchQuery, String searchQueryParameter) {
        Sort sort = Sort.by("username");
        if (sortDirection.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else if (sortDirection.equalsIgnoreCase("asc")) {
            sort = sort.ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        searchQuery = searchQuery == null ? "" : searchQuery;
        searchQueryParameter = searchQueryParameter == null ? "" : searchQueryParameter;
        Specification<User> specification = switch (searchQueryParameter) {
            case "username" -> UserSpecification.hasUsername(searchQuery);
            case "email" -> UserSpecification.hasEmail(searchQuery);
            case "firstName" -> UserSpecification.hasFirstName(searchQuery);
            case "lastName" -> UserSpecification.hasLastName(searchQuery);
            case "fullName" -> UserSpecification.hasFullName(searchQuery);
            default -> Specification.where(null);
        };

        return userRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User create(CreateUserDTO userDTO) throws ModelAlreadyExistsException {
        if (userRepository.existsByUsername(userDTO.username())) {
            throw new ModelAlreadyExistsException("User with same username already exists");
        }
        User user = new User();
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.username());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> update(Long id, User updatedUser) throws ModelAlreadyExistsException, ModelValidationException {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new ModelValidationException("User with id " + id + " does not exist");
        }

        if (!existingUser.get().getUsername().equals(updatedUser.getUsername()) && userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new ModelValidationException("Username already exists");
        }

        User user = existingUser.get();
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());

        return Optional.of(userRepository.save(user));
    }

    @Override
    public void delete(Long id) throws ModelNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new ModelNotFoundException("User with id " + id + " does not exist");
        }
        userRepository.deleteById(id);
    }
}
