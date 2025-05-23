package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.User;
import com.example.gamestoreclient.services.UserService;
import com.example.gamestoreclient.utils.AlertUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUsersController implements Initializable {
    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> createdAtColumn;

    @FXML
    private TableColumn<User, HBox> actionsColumn;

    private final UserService userService = new UserService();
    private ObservableList<User> users = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadUsers();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        createdAtColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getCreatedAt()));
            }
            return new SimpleStringProperty("N/A");
        });

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button makeAdminButton = new Button("Make Admin");
            private final Button makeUserButton = new Button("Make User");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(10);

            {
                makeAdminButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    changeUserRole(user, "ADMIN");
                });

                makeUserButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    changeUserRole(user, "USER");
                });

                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    buttonBox.getChildren().clear();

                    if ("USER".equals(user.getRole())) {
                        buttonBox.getChildren().addAll(makeAdminButton, deleteButton);
                    } else {
                        buttonBox.getChildren().addAll(makeUserButton, deleteButton);
                    }

                    setGraphic(buttonBox);
                }
            }
        });
    }

    private void loadUsers() {
        try {
            List<User> allUsers = userService.getAllUsers();

            users.clear();
            users.addAll(allUsers);
            usersTable.setItems(users);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load users: " + e.getMessage());
        }
    }

    private void changeUserRole(User user, String newRole) {
        try {
            user.setRole(newRole);
            userService.updateUser(user);

            // Refresh the table
            loadUsers();

            AlertUtils.showInfoAlert("Success", "Role Updated",
                    user.getName() + "'s role has been updated to " + newRole + ".");

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Update Error", "Could not update user role: " + e.getMessage());
        }
    }

    private void deleteUser(User user) {
        boolean confirmed = AlertUtils.showConfirmationAlert(
                "Delete User",
                "Confirm Deletion",
                "Are you sure you want to delete " + user.getName() + "? This action cannot be undone."
        );

        if (confirmed) {
            try {
                userService.deleteUser(user.getId());

                // Refresh the table
                loadUsers();

                AlertUtils.showInfoAlert("Success", "User Deleted",
                        user.getName() + " has been deleted successfully.");

            } catch (IOException e) {
                AlertUtils.showErrorAlert("Error", "Delete Error", "Could not delete user: " + e.getMessage());
            }
        }
    }
}
