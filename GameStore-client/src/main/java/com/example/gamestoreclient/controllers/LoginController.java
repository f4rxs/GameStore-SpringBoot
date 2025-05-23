package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.User;
import com.example.gamestoreclient.services.UserService;
import com.example.gamestoreclient.utils.AlertUtils;
import com.example.gamestoreclient.utils.SessionManager;
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
import java.net.URL;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Email and password are required");
            return;
        }

        try {
            System.out.println("Attempting login for: " + email);

            User user = userService.loginUser(email, password);

            // Debug: Check what we got back
            if (user == null) {
                System.err.println("ERROR: UserService.loginUser() returned null!");
                showError("Login failed: User service returned null");
                return;
            }

            System.out.println("UserService returned user: " + user);
            System.out.println("User ID: " + user.getId());
            System.out.println("User Name: " + (user.getName() != null ? user.getName() : "null"));
            System.out.println("User Email: " + (user.getEmail() != null ? user.getEmail() : "null"));
            System.out.println("User Role: " + (user.getRole() != null ? user.getRole() : "null"));

            // Store user in session
            SessionManager.getInstance().setCurrentUser(user);
            System.out.println("User stored in session");

            // Verify session storage
            User sessionUser = SessionManager.getInstance().getCurrentUser();
            if (sessionUser == null) {
                System.err.println("ERROR: User not properly stored in session!");
                showError("Session error: User not stored properly");
                return;
            }

            System.out.println("Session verification - User: " + sessionUser.getName());

            // Navigate to main dashboard
            navigateToDashboard(event);

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            showError("Login failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            System.out.println("Navigating to register...");

            // Try both possible paths
            URL registerResource = getClass().getResource("/com/example/gamestoreclient/fxml/register.fxml");
            if (registerResource == null) {
                registerResource = getClass().getResource("/com/example/gamestoreclient/fxml/Register.fxml");
            }

            if (registerResource == null) {
                System.err.println("Register.fxml not found in either location");
                AlertUtils.showErrorAlert("Error", "Navigation Error", "Register page not found");
                return;
            }

            Parent registerView = FXMLLoader.load(registerResource);
            Scene registerScene = new Scene(registerView);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(registerScene);
            stage.show();

            System.out.println("Successfully navigated to register");
        } catch (IOException e) {
            System.err.println("Error loading register page: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load register page: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void navigateToDashboard(ActionEvent event) {
        try {
            System.out.println("Navigating to dashboard...");

            // Check if dashboard.fxml exists
            URL dashboardResource = getClass().getResource("/com/example/gamestoreclient/fxml/dashboard.fxml");
            if (dashboardResource == null) {
                System.err.println("Dashboard.fxml not found at: /com/example/gamestoreclient/fxml/dashboard.fxml");
                AlertUtils.showErrorAlert("Error", "Navigation Error", "Dashboard page not found");
                return;
            }

            System.out.println("Dashboard resource found: " + dashboardResource.toString());

            Parent dashboardView = FXMLLoader.load(dashboardResource);
            Scene dashboardScene = new Scene(dashboardView);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.show();

            System.out.println("Successfully navigated to dashboard");
        } catch (IOException e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load dashboard: " + e.getMessage());
        }
    }
}