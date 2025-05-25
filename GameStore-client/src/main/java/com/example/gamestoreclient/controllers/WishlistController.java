package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.models.WishList;
import com.example.gamestoreclient.services.CartService;
import com.example.gamestoreclient.services.GameService;
import com.example.gamestoreclient.services.WishListService;
import com.example.gamestoreclient.utils.AlertUtils;
import com.example.gamestoreclient.utils.SessionManager;
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

public class WishlistController implements Initializable {
    @FXML
    private TableView<WishlistItem> wishlistTable;

    @FXML
    private TableColumn<WishlistItem, String> gameColumn;

    @FXML
    private TableColumn<WishlistItem, String> priceColumn;

    @FXML
    private TableColumn<WishlistItem, String> dateAddedColumn;

    @FXML
    private TableColumn<WishlistItem, HBox> actionsColumn;

    private final WishListService wishListService = new WishListService();
    private final GameService gameService = new GameService();
    private final CartService cartService = new CartService();

    private ObservableList<WishlistItem> wishlistItems = FXCollections.observableArrayList();
    private Map<Integer, Game> gameMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadWishlistItems();
    }

    private void setupTableColumns() {
        gameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getTitle()));

        priceColumn.setCellValueFactory(cellData -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return new SimpleStringProperty(currencyFormat.format(cellData.getValue().getGame().getPrice()));
        });

        dateAddedColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            return new SimpleStringProperty(dateFormat.format(cellData.getValue().getWishList().getAddedAt()));
        });

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button addToCartButton = new Button("Add to Cart");
            private final Button removeButton = new Button("Remove");
            private final HBox buttonBox = new HBox(10, addToCartButton, removeButton);

            {
                addToCartButton.setOnAction(event -> {
                    WishlistItem wishlistItem = getTableView().getItems().get(getIndex());
                    addToCart(wishlistItem);
                });

                removeButton.setOnAction(event -> {
                    WishlistItem wishlistItem = getTableView().getItems().get(getIndex());
                    removeFromWishlist(wishlistItem);
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

    private void loadWishlistItems() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            return;
        }

        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();

            // Get wishlist items
            List<WishList> wishlist = wishListService.getWishListByUserId(userId);

            // Get all games in one request
            List<Game> games = gameService.getAllGames();

            // Create a map of game ID to Game object for quick lookup
            for (Game game : games) {
                gameMap.put(game.getId(), game);
            }

            // Clear existing items
            wishlistItems.clear();

            // Add wishlist items to the observable list
            for (WishList wishlistItem : wishlist) {
                Game game = gameMap.get(wishlistItem.getGameId());
                if (game != null) {
                    wishlistItems.add(new WishlistItem(game, wishlistItem));
                }
            }

            // Update the table
            wishlistTable.setItems(wishlistItems);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Wishlist Error", "Could not load wishlist items: " + e.getMessage());
        }
    }

    private void addToCart(WishlistItem wishlistItem) {
        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            cartService.addToCartWithDefaultQuantity(userId, wishlistItem.getGame().getId());

            AlertUtils.showInfoAlert(
                    "Added to Cart",
                    "Game Added to Cart",
                    wishlistItem.getGame().getTitle() + " has been added to your cart."
            );

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Cart Error", "Could not add game to cart: " + e.getMessage());
        }
    }

    private void removeFromWishlist(WishlistItem wishlistItem) {
        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            wishListService.removeFromWishList(userId, wishlistItem.getGame().getId());

            // Update UI
            wishlistItems.remove(wishlistItem);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Wishlist Error", "Could not remove item from wishlist: " + e.getMessage());
        }
    }

    @FXML
    private void handleContinueShopping() {
        // Navigate back to the games view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gamestoreclient/fxml/dashboard.fxml"));
            Parent dashboardView = loader.load();

            DashboardController controller = loader.getController();

            // Get the current stage
            Stage stage = (Stage) wishlistTable.getScene().getWindow();
            stage.setScene(new Scene(dashboardView));

            // Show the games view
            controller.showGamesView(null);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not navigate to store: " + e.getMessage());
        }
    }

    public static class WishlistItem {
        private final Game game;
        private final WishList wishList;

        public WishlistItem(Game game, WishList wishList) {
            this.game = game;
            this.wishList = wishList;
        }

        public Game getGame() {
            return game;
        }

        public WishList getWishList() {
            return wishList;
        }
    }
}
