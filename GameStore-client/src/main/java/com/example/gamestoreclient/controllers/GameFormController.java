package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.models.Genre;
import com.example.gamestoreclient.services.GameService;
import com.example.gamestoreclient.services.GenreService;
import com.example.gamestoreclient.utils.AlertUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class GameFormController  implements Initializable {
    public enum FormMode {
        ADD,
        EDIT
    }

    @FXML
    private Label formTitleLabel;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<Genre> genreComboBox;

    @FXML
    private TextField imageUrlField;

    @FXML
    private CheckBox availableCheckBox;

    @FXML
    private DatePicker releaseDatePicker;

    @FXML
    private Button saveButton;

    private final GameService gameService = new GameService();
    private final GenreService genreService = new GenreService();

    private FormMode mode = FormMode.ADD;
    private Game game;
    private Runnable onGameSaved;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadGenres();
    }

    public void setMode(FormMode mode) {
        this.mode = mode;
        updateFormTitle();
    }

    public void setGame(Game game) {
        this.game = game;
        populateForm();
    }

    public void setOnGameSaved(Runnable callback) {
        this.onGameSaved = callback;
    }

    private void updateFormTitle() {
        formTitleLabel.setText(mode == FormMode.ADD ? "Add New Game" : "Edit Game");
        saveButton.setText(mode == FormMode.ADD ? "Add Game" : "Save Changes");
    }

    private void loadGenres() {
        try {
            List<Genre> genres = genreService.getAllGenres();
            genreComboBox.getItems().clear();
            genreComboBox.getItems().addAll(genres);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load genres: " + e.getMessage());
        }
    }

    private void populateForm() {
        if (game == null) return;

        titleField.setText(game.getTitle());
        descriptionArea.setText(game.getDescription());
        priceField.setText(String.valueOf(game.getPrice()));

        // Set selected genre
        for (Genre genre : genreComboBox.getItems()) {
            if (genre.getId() == game.getGenreId()) {
                genreComboBox.setValue(genre);
                break;
            }
        }

        imageUrlField.setText(game.getImageUrl());
        availableCheckBox.setSelected(game.isAvailable());

        // Set release date
        if (game.getReleaseDate() != null) {
            LocalDate releaseDate = game.getReleaseDate().toLocalDateTime().toLocalDate();
            releaseDatePicker.setValue(releaseDate);
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            // Create or update game object
            if (mode == FormMode.ADD) {
                game = new Game();
            }

            // Set game properties
            game.setTitle(titleField.getText().trim());
            game.setDescription(descriptionArea.getText().trim());
            game.setPrice(Double.parseDouble(priceField.getText().trim()));
            game.setGenreId(genreComboBox.getValue().getId());
            game.setImageUrl(imageUrlField.getText().trim());
            game.setAvailable(availableCheckBox.isSelected());

            // Set release date
            if (releaseDatePicker.getValue() != null) {
                Date releaseDate = Date.from(releaseDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                game.setReleaseDate(new Timestamp(releaseDate.getTime()));
            }

            // Save game
            if (mode == FormMode.ADD) {
                gameService.createGame(game);
                AlertUtils.showInfoAlert("Success", "Game Added", "The game has been added successfully.");
            } else {
                gameService.updateGame(game);
                AlertUtils.showInfoAlert("Success", "Game Updated", "The game has been updated successfully.");
            }

            // Call callback if provided
            if (onGameSaved != null) {
                onGameSaved.run();
            }

            // Close the form
            closeForm(event);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Save Error", "Could not save game: " + e.getMessage());
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("Error", "Invalid Input", "Please enter a valid price.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm(event);
    }

    private boolean validateForm() {
        if (titleField.getText().trim().isEmpty()) {
            AlertUtils.showWarningAlert("Validation Error", "Title Required", "Please enter a title for the game.");
            return false;
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            AlertUtils.showWarningAlert("Validation Error", "Description Required", "Please enter a description for the game.");
            return false;
        }

        if (priceField.getText().trim().isEmpty()) {
            AlertUtils.showWarningAlert("Validation Error", "Price Required", "Please enter a price for the game.");
            return false;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                AlertUtils.showWarningAlert("Validation Error", "Invalid Price", "Price cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtils.showWarningAlert("Validation Error", "Invalid Price", "Please enter a valid price.");
            return false;
        }

        if (genreComboBox.getValue() == null) {
            AlertUtils.showWarningAlert("Validation Error", "Genre Required", "Please select a genre for the game.");
            return false;
        }

        if (imageUrlField.getText().trim().isEmpty()) {
            AlertUtils.showWarningAlert("Validation Error", "Image URL Required", "Please enter an image URL for the game.");
            return false;
        }

        return true;
    }

    private void closeForm(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
