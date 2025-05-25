package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.User;
import com.example.gamestoreclient.services.UserService;
import com.example.gamestoreclient.utils.AlertUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;

public class RegisterController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleRegister(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate input
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        try {
            // Create user object
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole("USER"); // Default role
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Register user
            User registeredUser = userService.registerUser(user);

            // Show success message
            AlertUtils.showInfoAlert("Success", "Registration Successful",
                    "Your account has been created successfully. Please login.");

            // Navigate back to login
            handleBackToLogin(event);

        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("/com/example/gamestoreclient/fxml/login.fxml"));
            Scene loginScene = new Scene(loginView);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load login page: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
