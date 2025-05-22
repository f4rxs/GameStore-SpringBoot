package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.models.Genre;
import com.example.gamestoreclient.models.Review;
import com.example.gamestoreclient.services.*;
import com.example.gamestoreclient.utils.AlertUtils;
import com.example.gamestoreclient.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GameDetailsController {
    @FXML
    private Label titleLabel;

    @FXML
    private ImageView gameImage;

    @FXML
    private Label priceLabel;

    @FXML
    private Label genreLabel;

    @FXML
    private Label releaseDateLabel;

    @FXML
    private Label availabilityLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label averageRatingLabel;

    @FXML
    private ListView<Review> reviewsListView;

    private Game game;
    private final GameService gameService = new GameService();
    private final GenreService genreService = new GenreService();
    private final ReviewService reviewService = new ReviewService();
    private final CartService cartService = new CartService();
    private final WishListService wishListService = new WishListService();

    public void setGame(Game game) {
        this.game = game;
        loadGameDetails();
    }

    private void loadGameDetails() {
        if (game == null) return;

        // Set basic game info
        titleLabel.setText(game.getTitle());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        priceLabel.setText(currencyFormat.format(game.getPrice()));

        descriptionArea.setText(game.getDescription());

        // Set availability
        availabilityLabel.setText("Availability: " + (game.isAvailable() ? "In Stock" : "Out of Stock"));

        // Format release date
        if (game.getReleaseDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            releaseDateLabel.setText("Release Date: " + dateFormat.format(game.getReleaseDate()));
        }

        // Load game image
        try {
            gameImage.setImage(new Image(game.getImageUrl()));
        } catch (Exception e) {
            gameImage.setImage(new Image("/images/placeholder.png"));
        }

        // Load genre
        try {
            Genre genre = genreService.getGenreById(game.getGenreId());
            genreLabel.setText("Genre: " + genre.getName());
        } catch (IOException e) {
            genreLabel.setText("Genre: Unknown");
        }

        // Load reviews and average rating
        loadReviews();
    }

    private void loadReviews() {
        try {
            // Get average rating
            double averageRating = reviewService.getAverageRatingForGame(game.getId());
            averageRatingLabel.setText(String.format("Average Rating: %.1f/5", averageRating));

            // Get reviews
            List<Review> reviews = reviewService.getReviewsByGameId(game.getId());
            reviewsListView.getItems().clear();
            reviewsListView.getItems().addAll(reviews);

            // Set cell factory to customize review display
            reviewsListView.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(Review review, boolean empty) {
                    super.updateItem(review, empty);
                    if (empty || review == null) {
                        setText(null);
                    } else {
                        setText(String.format("Rating: %d/5 - %s", review.getRating(), review.getComment()));
                    }
                }
            });

        } catch (IOException e) {
            averageRatingLabel.setText("Average Rating: N/A");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAddToCart() {
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

    @FXML
    private void handleAddToWishlist() {
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

    @FXML
    private void handleWriteReview() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            AlertUtils.showWarningAlert("Login Required", "Authentication Required", "Please login to write a review.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/write-review.fxml"));
            Parent reviewView = loader.load();

            WriteReviewController controller = loader.getController();
            controller.setGame(game);
            controller.setOnReviewSubmitted(this::loadReviews);

            Stage reviewStage = new Stage();
            reviewStage.setTitle("Write a Review");
            reviewStage.setScene(new Scene(reviewView));
            reviewStage.show();
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not load review form: " + e.getMessage());
        }
    }
}
