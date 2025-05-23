package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.User;
import com.example.gamestoreclient.utils.AlertUtils;
import com.example.gamestoreclient.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Label userNameLabel;

    @FXML
    private Button adminButton;

    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Dashboard Controller initializing...");

        try {

            if (SessionManager.getInstance().isLoggedIn()) {
                User currentUser = SessionManager.getInstance().getCurrentUser();
                String displayName = (currentUser.getName() != null) ?
                        currentUser.getName() :
                        currentUser.getEmail().split("@")[0]; // Use email prefix if name is null
                userNameLabel.setText("Welcome, " + displayName);
                System.out.println("User set: " + displayName);
            } else {
                userNameLabel.setText("Welcome, Guest");
                System.out.println("No user logged in");
            }

            // Show admin button if user is admin
            adminButton.setVisible(SessionManager.getInstance().isAdmin());
            System.out.println("Admin button visible: " + SessionManager.getInstance().isAdmin());


             showGamesView(null);

//            Label welcomeLabel = new Label("Welcome to Game Store Dashboard!\nSelect an option from the sidebar.");
//            welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-alignment: center;");
//            contentArea.getChildren().clear();
//            contentArea.getChildren().add(welcomeLabel);

            System.out.println("Dashboard initialized successfully");

        } catch (Exception e) {
            System.err.println("Error initializing dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logout button clicked");

        // Clear session
        SessionManager.getInstance().logout();

        try {
            // Navigate back to login
            Parent loginView = FXMLLoader.load(getClass().getResource("/com/example/gamestoreclient/fxml/Login.fxml"));
            Scene loginScene = new Scene(loginView);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();

            System.out.println("Successfully navigated to login");
        } catch (IOException e) {
            System.err.println("Error loading login page: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load login page: " + e.getMessage());
        }
    }

    @FXML
    public void showGamesView(ActionEvent event) {
        System.out.println("Trying to load games view...");
        loadView("/com/example/gamestoreclient/fxml/games.fxml");
    }

    @FXML
    private void showLibraryView(ActionEvent event) {
        System.out.println("Trying to load library view...");
        loadView("/com/example/gamestoreclient/fxml/library.fxml");
    }

    @FXML
    private void showCartView(ActionEvent event) {
        System.out.println("Trying to load cart view...");
        loadView("/com/example/gamestoreclient/fxml/cart.fxml");
    }

    @FXML
    private void showWishlistView(ActionEvent event) {
        System.out.println("Trying to load wishlist view...");
        loadView("/com/example/gamestoreclient/fxml/wishlist.fxml");
    }

    @FXML
    private void showOrdersView(ActionEvent event) {
        System.out.println("Trying to load orders view...");
        loadView("/com/example/gamestoreclient/fxml/orders.fxml");
    }

    @FXML
    private void showAdminView(ActionEvent event) {
        System.out.println("Trying to load admin view...");
        loadView("/com/example/gamestoreclient/fxml/admin.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            System.out.println("Loading view: " + fxmlPath);

            // Check if resource exists
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Resource not found: " + fxmlPath);

                // Show a message instead of crashing
                Label errorLabel = new Label("View not available yet: " + fxmlPath);
                errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-text-alignment: center;");
                contentArea.getChildren().clear();
                contentArea.getChildren().add(errorLabel);
                return;
            }

            Parent view = FXMLLoader.load(resource);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            System.out.println("Successfully loaded view: " + fxmlPath);
        } catch (IOException e) {
            System.err.println("Error loading view " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();

            // error message to prevennt of crashing
            Label errorLabel = new Label("Error loading view: " + fxmlPath + "\n" + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-text-alignment: center;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(errorLabel);
        }
    }
}