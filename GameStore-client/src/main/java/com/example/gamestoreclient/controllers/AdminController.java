package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.utils.AlertUtils;
import com.example.gamestoreclient.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements  Initializable {
    @FXML
    private StackPane adminContentArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if user is admin
        if (!SessionManager.getInstance().isAdmin()) {
            AlertUtils.showErrorAlert("Access Denied",
                    "Unauthorized Access",
                    "You do not have permission to access the admin dashboard.");
            return;
        }
    }

    @FXML
    private void showManageGames(ActionEvent event) {
        loadAdminView("/com/example/gamestoreclient/fxml/admin-games.fxml");
    }

    @FXML
    private void showManageUsers(ActionEvent event) {
        loadAdminView("/com/example/gamestoreclient/fxml/admin-users.fxml");
    }

    @FXML
    private void showManageOrders(ActionEvent event) {
        loadAdminView("/com/example/gamestoreclient/fxml/admin-orders.fxml");
    }

    @FXML
    private void showManageGenres(ActionEvent event) {
        loadAdminView("/com/example/gamestoreclient/fxml/admin-genres.fxml");
    }

    private void loadAdminView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            adminContentArea.getChildren().clear();
            adminContentArea.getChildren().add(view);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load admin view: " + e.getMessage());
        }
    }
}
