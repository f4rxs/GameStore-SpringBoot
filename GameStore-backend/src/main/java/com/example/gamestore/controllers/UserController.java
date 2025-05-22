package com.example.gamestore.controllers;

import com.example.gamestore.models.LoginRequest;
import com.example.gamestore.models.User;
import com.example.gamestore.services.UserService;
import com.example.gamestore.utils.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users") // Tested ( 8 out of 8)
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping  // Tested
    public ResponseEntity<?> getAllUsers() {
        try {
            logger.info("Fetching all users");
            List<User> users = userService.findAllUsers();
            if (users.isEmpty()) {
                logger.warn("No users found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("No users found", 404));
            }
            logger.debug("Found {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching all users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching users", 500));
        }
    }

    @GetMapping("/{id}") // Tested
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        logger.info("Fetching user with id: {}", id);
        try {
            Optional<User> user = userService.findUserById(id);
            if (user.isPresent()) {
                logger.debug("Found user: {}", user.get().getEmail());
                return ResponseEntity.ok(user.get());
            }
            logger.warn("User not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found with id: " + id, 404));
        } catch (Exception e) {
            logger.error("Error fetching user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching user", 500));
        }
    }

    @GetMapping("/email/{email}") // Tested
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        logger.info("Fetching user with email: {}", email);
        try {
            Optional<User> user = userService.findUserByEmail(email);
            if (user.isPresent()) {
                logger.debug("Found user by email: {}", email);
                return ResponseEntity.ok(user.get());
            }
            logger.warn("User not found with email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found with email: " + email, 404));
        } catch (Exception e) {
            logger.error("Error fetching user with email {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching user", 500));
        }
    }

    @GetMapping("/name/{name}") // Tested
    public ResponseEntity<?> getUserByName(@PathVariable String name) {
        logger.info("Fetching user with name: {}", name);
        try {
            Optional<User> user = userService.findUserByName(name);
            if (user.isPresent()) {
                logger.debug("Found user by name: {}", name);
                return ResponseEntity.ok(user.get());
            }
            logger.warn("User not found with name: {}", name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found with name: " + name, 404));
        } catch (Exception e) {
            logger.error("Error fetching user with name {}: {}", name, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching user", 500));
        }
    }

    @PostMapping("/register") // Tested
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        logger.info("Attempting to register new user with email: {}", user.getEmail());
        try {
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }
            User registeredUser = userService.registerUser(user);
            logger.info("Successfully registered user with id: {}", registeredUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid registration attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Registration failed", 500));
        }
    }

    @PostMapping("/login") // Tested
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getEmail());
        try {
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                logger.warn("Login attempt with missing credentials");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Email and password are required", 400));
            }

            Optional<User> user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            if (user.isPresent()) {
                logger.info("Successful login for user: {}", loginRequest.getEmail());
                return ResponseEntity.ok(user.get());
            }

            logger.warn("Failed login attempt for user: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials", 401));
        } catch (Exception e) {
            logger.error("Error during login attempt for {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Login failed", 500));
        }
    }

    @PutMapping("/{id}") // Tested
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
        logger.info("Attempting to update user with id: {}", id);
        try {

            if (user.getName() == null && user.getEmail() == null && user.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("At least one field (name, email, or password) must be provided", 400));
            }

            user.setId(id);
            User updatedUser = userService.updateUser(user);
            logger.info("Successfully updated user with id: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid update attempt for user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            logger.error("Error updating user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Update failed: " + e.getMessage(), 500));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        logger.info("Attempting to delete user with id: {}", id);
        try {
            userService.deleteUser(id);
            logger.info("Successfully deleted user with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error deleting user", 500));
        }
    }
}
