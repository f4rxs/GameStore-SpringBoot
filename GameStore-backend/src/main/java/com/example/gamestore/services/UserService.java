package com.example.gamestore.services;

import com.example.gamestore.models.User;
import com.example.gamestore.repositories.UserRepository;
import com.example.gamestore.utils.PasswordUtils;
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

        return userRepository.findByName(name);
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Hash the password
        String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

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
        if (user.isPresent() && PasswordUtils.checkPassword(password, user.get().getPassword())) {
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


        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
        } else {
            user.setPassword(existingUser.getPassword());
        }


        if (user.getCreatedAt() == null) {
            user.setCreatedAt(existingUser.getCreatedAt());
        }

        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}