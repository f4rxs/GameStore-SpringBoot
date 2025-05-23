package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.models.Genre;
import com.example.gamestoreclient.services.GameService;
import com.example.gamestoreclient.services.GenreService;
import com.example.gamestoreclient.utils.AlertUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminGamesController implements  Initializable {

    @FXML
    private TableView<Game> gamesTable;

    @FXML
    private TableColumn<Game, Integer> idColumn;

    @FXML
    private TableColumn<Game, String> titleColumn;

    @FXML
    private TableColumn<Game, String> genreColumn;

    @FXML
    private TableColumn<Game, String> priceColumn;

    @FXML
    private TableColumn<Game, Boolean> availableColumn;

    @FXML
    private TableColumn<Game, String> releaseDateColumn;

    @FXML
    private TableColumn<Game, HBox> actionsColumn;

    private final GameService gameService = new GameService();
    private final GenreService genreService = new GenreService();

    private ObservableList<Game> games = FXCollections.observableArrayList();
    private Map<Integer, Genre> genreMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadGenres();
        loadGames();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));

        genreColumn.setCellValueFactory(cellData -> {
            Genre genre = genreMap.get(cellData.getValue().getGenreId());
            return new SimpleStringProperty(genre != null ? genre.getName() : "Unknown");
        });

        priceColumn.setCellValueFactory(cellData -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return new SimpleStringProperty(currencyFormat.format(cellData.getValue().getPrice()));
        });

        availableColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isAvailable()));

        releaseDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getReleaseDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getReleaseDate()));
            }
            return new SimpleStringProperty("N/A");
        });

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Game game = getTableView().getItems().get(getIndex());
                    editGame(game);
                });

                deleteButton.setOnAction(event -> {
                    Game game = getTableView().getItems().get(getIndex());
                    deleteGame(game);
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
            List<Genre> genres = genreService.getAllGenres();

            // Create a map of genre ID to Genre object for quick lookup
            for (Genre genre : genres) {
                genreMap.put(genre.getId(), genre);
            }

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load genres: " + e.getMessage());
        }
    }

    private void loadGames() {
        try {
            List<Game> allGames = gameService.getAllGames();

            games.clear();
            games.addAll(allGames);
            gamesTable.setItems(games);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load games: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gamestoreclient/fxml/game-form.fxml"));
            Parent formView = loader.load();

            GameFormController controller = loader.getController();
            controller.setMode(GameFormController.FormMode.ADD);
            controller.setOnGameSaved(this::loadGames);

            Stage formStage = new Stage();
            formStage.setTitle("Add New Game");
            formStage.setScene(new Scene(formView));
            formStage.show();

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load game form: " + e.getMessage());
        }
    }

    private void editGame(Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gamestoreclient/fxml/game-form.fxml"));
            Parent formView = loader.load();

            GameFormController controller = loader.getController();
            controller.setMode(GameFormController.FormMode.EDIT);
            controller.setGame(game);
            controller.setOnGameSaved(this::loadGames);

            Stage formStage = new Stage();
            formStage.setTitle("Edit Game");
            formStage.setScene(new Scene(formView));
            formStage.show();

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load game form: " + e.getMessage());
        }
    }

    private void deleteGame(Game game) {
        boolean confirmed = AlertUtils.showConfirmationAlert(
                "Delete Game",
                "Confirm Deletion",
                "Are you sure you want to delete " + game.getTitle() + "? This action cannot be undone."
        );

        if (confirmed) {
            try {
                gameService.deleteGame(game.getId());

                // Refresh the table
                loadGames();

                AlertUtils.showInfoAlert("Success", "Game Deleted", game.getTitle() + " has been deleted successfully.");

            } catch (IOException e) {
                AlertUtils.showErrorAlert("Error", "Delete Error", "Could not delete game: " + e.getMessage());
            }
        }
    }
}
