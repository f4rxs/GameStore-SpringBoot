package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.models.Genre;
import com.example.gamestoreclient.services.CartService;
import com.example.gamestoreclient.services.GameService;
import com.example.gamestoreclient.services.GenreService;
import com.example.gamestoreclient.services.WishListService;
import com.example.gamestoreclient.utils.AlertUtils;
import com.example.gamestoreclient.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GamesController implements Initializable {

    @FXML
    private ComboBox<Genre> genreFilter;

    @FXML
    private TextField searchField;

    @FXML
    private FlowPane gamesFlowPane;

    @FXML
    private ToggleGroup sortToggleGroup;

    @FXML
    private RadioButton sortByPriceRadio;

    @FXML
    private RadioButton sortByDateRadio;

    private final GameService gameService = new GameService();
    private final GenreService genreService = new GenreService();
    private final CartService cartService = new CartService();
    private final WishListService wishListService = new WishListService();

    private List<Game> allGames;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadGenres();
        loadGames();
        setupSortListener();
        setupGenreFilterListener();
    }

    private void setupSortListener() {
        sortToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String sortBy = sortByPriceRadio.isSelected() ? "price" : "date";
                updateGameDisplay(sortBy);
            }
        });
    }

    private void setupGenreFilterListener() {
        genreFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filterAndDisplayGames();
        });
    }

    private void loadGenres() {
        try {
            List<Genre> genres = genreService.getAllGenres();
            genreFilter.getItems().clear();
            genreFilter.getItems().add(null); // Add "All Genres" option
            genreFilter.getItems().addAll(genres);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load genres: " + e.getMessage());
        }
    }

    private void loadGames() {
        try {
            allGames = gameService.getAllGames();
            displayGames(allGames);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load games: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        filterAndDisplayGames();
    }

    private void filterAndDisplayGames() {
        if (allGames == null) return;

        List<Game> filteredGames = allGames;

        // Filter by search term
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            filteredGames = filteredGames.stream()
                    .filter(game -> game.getTitle().toLowerCase().contains(searchTerm))
                    .collect(Collectors.toList());
        }

        // Filter by genre
        Genre selectedGenre = genreFilter.getSelectionModel().getSelectedItem();
        if (selectedGenre != null) {
            filteredGames = filteredGames.stream()
                    .filter(game -> game.getGenreId() == selectedGenre.getId())
                    .collect(Collectors.toList());
        }

        displayGames(filteredGames);
    }

    private void updateGameDisplay(String sortBy) {
        if (allGames == null) return;

        List<Game> sortedGames = allGames.stream().collect(Collectors.toList());

        if ("price".equals(sortBy)) {
            sortedGames.sort(Comparator.comparingDouble(Game::getPrice));
        } else if ("date".equals(sortBy)) {
            sortedGames.sort(Comparator.comparing(Game::getReleaseDate));
        }

        displayGames(sortedGames);
    }

    private void displayGames(List<Game> games) {
        gamesFlowPane.getChildren().clear();

        if (games.isEmpty()) {
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50));
            emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px;");

            Label emptyLabel = new Label("No games found");
            emptyLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-text-fill: #374151;");

            Label suggestionLabel = new Label("Try adjusting your search or filters");
            suggestionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748b;");

            emptyBox.getChildren().addAll(emptyLabel, suggestionLabel);
            gamesFlowPane.getChildren().add(emptyBox);
        } else {
            for (Game game : games) {
                VBox gameCard = createGameCard(game);
                gamesFlowPane.getChildren().add(gameCard);
            }
        }
    }

    private VBox createGameCard(Game game) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);
        card.setPrefHeight(320);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e2e8f0; " +
                        "-fx-border-width: 1px; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-border-radius: 12px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.08), 8, 0, 0, 2);"
        );

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #e2e8f0; " +
                            "-fx-border-width: 1px; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-radius: 12px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 16, 0, 0, 4); " +
                            "-fx-scale-x: 1.02; " +
                            "-fx-scale-y: 1.02;"
            );
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #e2e8f0; " +
                            "-fx-border-width: 1px; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-radius: 12px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.08), 8, 0, 0, 2);"
            );
        });

        // Game image
        ImageView imageView = new ImageView();
        try {
            // Load image based on game title, similar to old project
            String imageName = game.getTitle();
            Image image = new Image(getClass().getResource("/com/example/gamestoreclient/images/" + imageName + ".png").toExternalForm());
            imageView.setImage(image);
        } catch (Exception e) {
            // Use placeholder image if the specific game image is not found
            try {
                imageView.setImage(new Image(getClass().getResource("/com/example/gamestoreclient/images/game_placeholder.png").toExternalForm()));
            } catch (Exception ex) {
                // Create a simple colored rectangle as fallback
                imageView.setFitWidth(170);
                imageView.setFitHeight(170);
            }
        }

        imageView.setFitWidth(170);
        imageView.setFitHeight(170);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-radius: 8px;");

        // Game title
        Label titleLabel = new Label(game.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b; -fx-wrap-text: true;");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(170);
        titleLabel.setWrapText(true);

        // Game price
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        Label priceLabel = new Label(currencyFormat.format(game.getPrice()));
        priceLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #10b981; -fx-font-weight: 700;");

        // Action buttons
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);

        Button wishlistButton = new Button("♡");
        wishlistButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #ef4444; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 5 10; " +
                        "-fx-border-color: #ef4444; " +
                        "-fx-border-width: 1; " +
                        "-fx-background-radius: 50; " +
                        "-fx-border-radius: 50; " +
                        "-fx-cursor: hand;"
        );
        wishlistButton.setOnAction(e -> {
            addToWishlist(game);
            wishlistButton.setText("♥");
            wishlistButton.setStyle(
                    "-fx-background-color: #ef4444; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 16px; " +
                            "-fx-padding: 5 10; " +
                            "-fx-border-color: #ef4444; " +
                            "-fx-border-width: 1; " +
                            "-fx-background-radius: 50; " +
                            "-fx-border-radius: 50; " +
                            "-fx-cursor: hand;"
            );
        });

        Button cartButton = new Button("🛒");
        cartButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #3b82f6; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 5 10; " +
                        "-fx-border-color: #3b82f6; " +
                        "-fx-border-width: 1; " +
                        "-fx-background-radius: 50; " +
                        "-fx-border-radius: 50; " +
                        "-fx-cursor: hand;"
        );
        cartButton.setOnAction(e -> addToCart(game));

        actionBox.getChildren().addAll(wishlistButton, cartButton);

        // View details button
        Button viewButton = new Button("View Details");
        viewButton.setStyle(
                "-fx-background-color: #f8fafc; " +
                        "-fx-text-fill: #334155; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-border-color: #cbd5e1; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-border-width: 1px; " +
                        "-fx-padding: 8px 16px; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"
        );

// Add hover effect
        viewButton.setOnMouseEntered(e -> viewButton.setStyle(
                "-fx-background-color: #f1f5f9; " +
                        "-fx-text-fill: #1e293b; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-border-color: #94a3b8; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-border-width: 1px; " +
                        "-fx-padding: 8px 16px; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"
        ));

        viewButton.setOnMouseExited(e -> viewButton.setStyle(
                "-fx-background-color: #f8fafc; " +
                        "-fx-text-fill: #334155; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-border-color: #cbd5e1; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-border-width: 1px; " +
                        "-fx-padding: 8px 16px; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"
        ));
        viewButton.setPrefWidth(170);
        viewButton.setOnAction(e -> showGameDetails(game));

        card.getChildren().addAll(imageView, titleLabel, priceLabel, actionBox, viewButton);

        return card;
    }

    private void addToWishlist(Game game) {
        if (!SessionManager.getInstance().isLoggedIn()) {
            AlertUtils.showWarningAlert("Login Required", "Authentication Required", "Please login to add items to your wishlist.");
            return;
        }

        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            com.example.gamestoreclient.models.WishList wishList = new com.example.gamestoreclient.models.WishList(
                    userId, game.getId(), new Timestamp(System.currentTimeMillis())
            );
            wishListService.addToWishList(wishList);
            AlertUtils.showInfoAlert("Success", "Added to Wishlist", game.getTitle() + " has been added to your wishlist.");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Wishlist Error", "Could not add game to wishlist: " + e.getMessage());
        }
    }

    private void addToCart(Game game) {
        if (!SessionManager.getInstance().isLoggedIn()) {
            AlertUtils.showWarningAlert("Login Required", "Authentication Required", "Please login to add items to your cart.");
            return;
        }

        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            cartService.addToCartWithDefaultQuantity(userId, game.getId());
            AlertUtils.showInfoAlert("Success", "Added to Cart", game.getTitle() + " has been added to your cart.");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Cart Error", "Could not add game to cart: " + e.getMessage());
        }
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
}
