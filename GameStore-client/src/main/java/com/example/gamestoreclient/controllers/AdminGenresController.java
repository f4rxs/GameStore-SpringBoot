package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Genre;
import com.example.gamestoreclient.services.GenreService;
import com.example.gamestoreclient.utils.AlertUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminGenresController implements Initializable {
    @FXML
    private TableView<Genre> genresTable;

    @FXML
    private TableColumn<Genre, Integer> idColumn;

    @FXML
    private TableColumn<Genre, String> nameColumn;


    @FXML
    private TableColumn<Genre, HBox> actionsColumn;

    private final GenreService genreService = new GenreService();
    private ObservableList<Genre> genres = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadGenres();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));


        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Genre genre = getTableView().getItems().get(getIndex());
                    showGenreDialog(genre);
                });

                deleteButton.setOnAction(event -> {
                    Genre genre = getTableView().getItems().get(getIndex());
                    deleteGenre(genre);
                });
            }

            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
    }

    private void loadGenres() {
        try {
            List<Genre> allGenres = genreService.getAllGenres();

            genres.clear();
            genres.addAll(allGenres);
            genresTable.setItems(genres);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load genres: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddGenre() {
        showGenreDialog(null);
    }

    private void showGenreDialog(Genre genre) {
        // Create the custom dialog
        Dialog<Genre> dialog = new Dialog<>();
        dialog.setTitle(genre == null ? "Add New Genre" : "Edit Genre");
        dialog.setHeaderText(genre == null ? "Create a new genre" : "Edit genre details");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Genre name");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Genre description");
        descriptionArea.setPrefRowCount(3);

        // Populate fields if editing
        if (genre != null) {
            nameField.setText(genre.getName());
            descriptionArea.setText(genre.getDescription());
        }

        // Add fields to the grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field by default
        nameField.requestFocus();

        // Convert the result to a genre when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (nameField.getText().trim().isEmpty()) {
                    AlertUtils.showWarningAlert("Validation Error", "Name Required", "Please enter a name for the genre.");
                    return null;
                }

                Genre result = genre == null ? new Genre() : genre;
                result.setName(nameField.getText().trim());
                result.setDescription(descriptionArea.getText().trim());
                return result;
            }
            return null;
        });

        Optional<Genre> result = dialog.showAndWait();

        result.ifPresent(newGenre -> {
            try {
                if (genre == null) {
                    // Create new genre
                    genreService.createGenre(newGenre);
                    AlertUtils.showInfoAlert("Success", "Genre Added", "The genre has been added successfully.");
                } else {
                    // Update existing genre
                    genreService.updateGenre(newGenre);
                    AlertUtils.showInfoAlert("Success", "Genre Updated", "The genre has been updated successfully.");
                }

                // Refresh the table
                loadGenres();

            } catch (IOException e) {
                AlertUtils.showErrorAlert("Error", "Save Error", "Could not save genre: " + e.getMessage());
            }
        });
    }

    private void deleteGenre(Genre genre) {
        boolean confirmed = AlertUtils.showConfirmationAlert(
                "Delete Genre",
                "Confirm Deletion",
                "Are you sure you want to delete the genre '" + genre.getName() + "'? This may affect games that use this genre."
        );

        if (confirmed) {
            try {
                genreService.deleteGenre(genre.getId());

                // Refresh the table
                loadGenres();

                AlertUtils.showInfoAlert("Success", "Genre Deleted",
                        "The genre '" + genre.getName() + "' has been deleted successfully.");

            } catch (IOException e) {
                AlertUtils.showErrorAlert("Error", "Delete Error", "Could not delete genre: " + e.getMessage());
            }
        }
    }
}
