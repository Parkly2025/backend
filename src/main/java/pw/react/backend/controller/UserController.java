package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.dto.CreateUserDTO;
import pw.react.backend.dto.LoginDTO;
import pw.react.backend.exceptions.ModelAlreadyExistsException;
import pw.react.backend.exceptions.ModelNotFoundException;
import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.User;
import pw.react.backend.services.UserService;
import pw.react.backend.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations related to users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/page/{page}")
    @Operation(summary = "Get all users (paginated - Admin Only)", description = "Retrieves a paginated list of users. Requires Admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of users",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges (Admin role required)")
    })
    public ResponseEntity<?> getAllUsers(
            @Parameter(description = "Page number (starting from 0)", required = true) @PathVariable int page,
            @Parameter(description = "Number of users per page.") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @Parameter(description = "Sorting direction (asc or desc). Allowed: {\"asc\", \"desc\"}.") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection,
            @Parameter(description = "Search query string") @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @Parameter(description = "Specific parameter to search within. Allowed: ['username', 'email', 'firstName', 'lastName', 'fullName']") @RequestParam(value = "searchQueryParameter", required = false) String searchQueryParameter) {
        return ResponseEntity.ok(userService.findAll(page, size, sortDirection, searchQuery, searchQueryParameter));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by its ID. Requires Admin or User role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges (Admin or User role required)"),
            @ApiResponse(responseCode = "404", description = "Not Found - User with the specified ID does not exist")
    })
    public ResponseEntity<?> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user based on the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid user data or user already exists",
                    content = @Content(mediaType = "text/plain"))
    })
    public ResponseEntity<?> createUser(
            @Parameter(description = "User object to create", required = true, schema = @Schema(implementation = CreateUserDTO.class)) @RequestBody CreateUserDTO userDTO) {
        try {
            User savedUser = userService.create(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ModelAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user", description = "Updates an existing user based on the provided ID and data. Requires Admin or User role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid user data or validation error",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges (Admin or User role required)"),
            @ApiResponse(responseCode = "404", description = "Not Found - User with the specified ID does not exist",
                    content = @Content(mediaType = "text/plain"))
    })
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable Long id,
            @Parameter(description = "Updated user object", required = true, schema = @Schema(implementation = User.class)) @RequestBody User updatedUser) {
        try {
            return new ResponseEntity<>(userService.update(id, updatedUser), HttpStatus.OK);
        } catch (ModelValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ModelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user based on the provided ID. Requires Admin or User role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges (Admin or User role required)"),
            @ApiResponse(responseCode = "404", description = "Not Found - User with the specified ID does not exist")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable Long id) {
        try {
            userService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ModelNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and sets a cookie containing the user's role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful."), // Document the Set-Cookie header
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid credentials",
                    content = @Content(mediaType = "text/plain"))
    })
    public ResponseEntity<?> loginUser(@Parameter(description = "Login credentials", required = true, schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoginDTO.class)) @RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.login(loginDTO);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("userId", user.getId());
            responseBody.put("username", user.getUsername());
            responseBody.put("role", user.getRole().name());
            responseBody.put("email", user.getEmail());

            return ResponseEntity.ok()
                    .body(responseBody);
        } catch (ModelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
