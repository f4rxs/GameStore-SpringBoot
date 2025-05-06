package com.services;

import com.models.User;
import com.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(User.Role.CUSTOMER);
        }

        // Set creation timestamp
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public User updateUser(User user) {
        if (user.getId() == 0) {
            throw new IllegalArgumentException("User ID must be provided for update");
        }

        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (existingUserOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + user.getId());
        }

        User existingUser = existingUserOpt.get();

        // Only update password if it's provided and different
        if (user.getPassword() != null && !user.getPassword().isEmpty() &&
                !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }

        // Preserve creation timestamp
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(existingUser.getCreatedAt());
        }

        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}