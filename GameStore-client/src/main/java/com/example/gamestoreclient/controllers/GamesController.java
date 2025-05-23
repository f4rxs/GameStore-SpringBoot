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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class GamesController implements Initializable {
    @FXML
    private ComboBox<Genre> genreFilter;

    @FXML
    private TextField searchField;

    @FXML
    private GridPane gamesGrid;

    private final GameService gameService = new GameService();
    private final GenreService genreService = new GenreService();
    private final CartService cartService = new CartService();
    private final WishListService wishListService = new WishListService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadGenres();
        loadGames();

        // Add listener to genre filter
        genreFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadGamesByGenre(newVal.getId());
            } else {
                loadGames();
            }
        });
    }

    private void loadGenres() {
        try {
            List<Genre> genres = genreService.getAllGenres();
            genreFilter.getItems().clear();
            genreFilter.getItems().add(null); // Add null option for "All Genres"
            genreFilter.getItems().addAll(genres);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load genres: " + e.getMessage());
        }
    }

    private void loadGames() {
        try {
            List<Game> games = gameService.getAllGames();
            displayGames(games);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load games: " + e.getMessage());
        }
    }

    private void loadGamesByGenre(int genreId) {
        try {
            List<Game> games = gameService.getGamesByGenre(genreId);
            displayGames(games);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load games by genre: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadGames();
            return;
        }

        try {
            List<Game> games = gameService.searchGamesByTitle(searchTerm);
            displayGames(games);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Search Error", "Could not search games: " + e.getMessage());
        }
    }

    private void displayGames(List<Game> games) {
        gamesGrid.getChildren().clear();

        int row = 0;
        int col = 0;
        int maxCols = 3;

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
            imageView.setImage(new Image(game.getImageUrl()));
        } catch (Exception e) {
            // Use placeholder image if the game image URL is invalid
//            imageView.setImage(new Image("/images/placeholder.png"));
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        // Game title
        Label titleLabel = new Label(game.getTitle());
        titleLabel.getStyleClass().add("game-title");

        // Game price
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        Label priceLabel = new Label(currencyFormat.format(game.getPrice()));
        priceLabel.getStyleClass().add("game-price");

        // Action buttons
        Button detailsButton = new Button("Details");
        detailsButton.setOnAction(e -> showGameDetails(game));

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> addToCart(game));

        Button addToWishlistButton = new Button("Add to Wishlist");
        addToWishlistButton.setOnAction(e -> addToWishlist(game));

        // Add all components to the card
        gameCard.getChildren().addAll(
                imageView,
                titleLabel,
                priceLabel,
                detailsButton,
                addToCartButton,
                addToWishlistButton
        );

        return gameCard;
    }

    private void showGameDetails(Game game) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game-details.fxml"));
//            Parent detailsView = loader.load();
//
//            GameDetailsController controller = loader.getController();
//            controller.setGame(game);
//
//            Stage detailsStage = new Stage();
//            detailsStage.setTitle(game.getTitle() + " - Details");
//            detailsStage.setScene(new Scene(detailsView));
//            detailsStage.show();
//        } catch (IOException e) {
//            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load game details: " + e.getMessage());
//        }
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
}
