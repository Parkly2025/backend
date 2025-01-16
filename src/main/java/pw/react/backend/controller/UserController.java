package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.dto.CreateUserDTO;
import pw.react.backend.dto.LoginDTO;
import pw.react.backend.exceptions.ModelAlreadyExistsException;
import pw.react.backend.exceptions.ModelNotFoundException;
import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.User;
import pw.react.backend.services.UserService;
import pw.react.backend.utils.ProtectedEndpoint;
import pw.react.backend.utils.Utils;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations related to users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users paginated", description = "Retrieves a paginated list of users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of users",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid page number or size")
    })
    @GetMapping("/page/{page}")
    public Page<User> getAllUsers(
        @Parameter(description = "Page number (0-based)", required = true) @PathVariable int page,
        @Parameter(description = "Page size") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
        @Parameter(description = "Sort direction (asc or desc)") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection,
        @Parameter(description = "Search query") @RequestParam(value = "searchQuery", required = false) String searchQuery,
        /*TODO: list User properties allowed to be searched on (e.g. username, firstName) ...*/
        @Parameter(description = "Search query property / parameter") @RequestParam(value = "searchQueryParameter", required = false) String searchQueryParameter)
    {
        return userService.findAll(page, size, sortDirection, searchQuery, searchQueryParameter);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Create a new user", description = "Creates a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - User already exists or invalid input")
    })
    @PostMapping
    public ResponseEntity<?> createUser(@Parameter(description = "User object to create", required = true, schema = @Schema(implementation = CreateUserDTO.class)) @RequestBody CreateUserDTO userDTO) {
        try {
            User savedUser = userService.create(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ModelAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Update an existing user", description = "Updates an existing user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - User already exists, invalid input, or invalid ID"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable Long id,
            @Parameter(description = "Updated user object", required = true, schema = @Schema(implementation = User.class)) @RequestBody User updatedUser,
            @CookieValue(value = "userRole", required = false) String userRole) {
        if (!Utils.roleAdminOrUser(userRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try {
            return new ResponseEntity<>(userService.update(id, updatedUser), HttpStatus.OK);
        } catch (ModelValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ModelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID of the user to delete", required = true) @PathVariable Long id,
                                           @CookieValue(value = "userRole", required = false) String userRole) {
        if (!Utils.roleAdminOrUser(userRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try {
                userService.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (ModelNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

    @Operation(summary = "Login user", description = "Authenticates a user and sets a cookie containing the user's role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login",
                    headers = @Header(
                            name = HttpHeaders.SET_COOKIE,
                            description = "Cookie containing the user's role. Example: userRole=ADMIN; Max-Age=36000",
                            schema = @Schema(type = "string")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. User not found",
                    content = @Content(schema = @Schema(type = "string"))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.login(loginDTO);
            ResponseCookie cookie = ResponseCookie.from("userRole", user.getRole().name()).
                    maxAge(10 * 60 * 60).path("/").httpOnly(true).build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .build();
        } catch (ModelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
