package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.services.GameService;
import com.example.gamestoreclient.utils.AlertUtils;
import com.example.gamestoreclient.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LibraryController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private GridPane gamesGrid;

    private final GameService gameService = new GameService();
    private List<Game> libraryGames = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLibraryGames();
    }

    private void loadLibraryGames() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            return;
        }

        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            libraryGames = gameService.getPurchasedGamesForUser(userId);
            displayGames(libraryGames);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load your game library: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            displayGames(libraryGames);
            return;
        }

        List<Game> filteredGames = libraryGames.stream()
                .filter(game -> game.getTitle().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        displayGames(filteredGames);
    }

    private void displayGames(List<Game> games) {
        gamesGrid.getChildren().clear();

        if (games.isEmpty()) {
            Label emptyLabel = new Label("No games in your library yet. Purchase some games to see them here!");
            emptyLabel.getStyleClass().add("empty-message");
            gamesGrid.add(emptyLabel, 0, 0);
            return;
        }

        int row = 0;
        int col = 0;
        int maxCols = 3; // Number of columns in the grid

        for (Game game : games) {
            VBox gameCard = createGameCard(game);
            gamesGrid.add(gameCard, col, row);

            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createGameCard(Game game) {
        VBox gameCard = new VBox(10);
        gameCard.getStyleClass().add("game-card");
        gameCard.setPadding(new Insets(10));

        // Game image
        ImageView imageView = new ImageView();
        try {
            // Load image based on game title, similar to old project
            String imageName = game.getTitle();
            Image image = new Image(getClass().getResource("/com/example/gamestoreclient/images/" + imageName + ".png").toExternalForm());
            imageView.setImage(image);
        } catch (Exception e) {
            try {
                imageView.setImage(new Image(getClass().getResource("/com/example/gamestoreclient/images/game_placeholder.png").toExternalForm()));
            } catch (Exception ex) {
                imageView.setFitWidth(150);
                imageView.setFitHeight(200);
            }
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        // Game title
        Label titleLabel = new Label(game.getTitle());
        titleLabel.getStyleClass().add("game-title");

        // Action buttons
        Button detailsButton = new Button("Details");
        detailsButton.setOnAction(e -> showGameDetails(game));

        Button playButton = new Button("Play Game");
        playButton.getStyleClass().add("primary-button");
        playButton.setOnAction(e -> playGame(game));

        // Add all components to the card
        gameCard.getChildren().addAll(
                imageView,
                titleLabel,
                detailsButton,
                playButton
        );

        return gameCard;
    }

    private void showGameDetails(Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gamestoreclient/fxml/game-details.fxml"));
            Parent detailsView = loader.load();

            GameDetailsController controller = loader.getController();
            controller.setGame(game);

            Stage detailsStage = new Stage();
            detailsStage.setTitle(game.getTitle() + " - Details");
            detailsStage.setScene(new Scene(detailsView));
            detailsStage.show();
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load game details: " + e.getMessage());
        }
    }

    private void playGame(Game game) {
        // In a real application, this would launch the game
        // For this demo, we'll just show a message
        AlertUtils.showInfoAlert(
                "Launch Game",
                "Playing " + game.getTitle(),
                "This is a demo. In a real application, the game would launch now."
        );
    }
}
