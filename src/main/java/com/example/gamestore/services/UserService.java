package com.example.gamestore.services;

import com.example.gamestore.models.User;
import com.example.gamestore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public Optional<User> findUserByName(String name) {
        // This is a custom query we need to add to the repository
        return userRepository.findByName(name);
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

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
        Optional<User> user = findUserByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
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
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
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