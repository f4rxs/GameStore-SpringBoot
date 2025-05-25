package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Cart;
import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.services.CartService;
import com.example.gamestoreclient.services.GameService;
import com.example.gamestoreclient.services.OrderService;
import com.example.gamestoreclient.utils.AlertUtils;
import com.example.gamestoreclient.utils.SessionManager;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class CartController implements  Initializable {

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> gameColumn;

    @FXML
    private TableColumn<CartItem, String> priceColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, String> totalColumn;

    @FXML
    private TableColumn<CartItem, Button> actionsColumn;

    @FXML
    private Label totalPriceLabel;

    private final CartService cartService = new CartService();
    private final GameService gameService = new GameService();
    private final OrderService orderService = new OrderService();

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private Map<Integer, Game> gameMap = new HashMap<>();
    private double cartTotal = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadCartItems();
    }

    private void setupTableColumns() {
        gameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getTitle()));

        priceColumn.setCellValueFactory(cellData -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return new SimpleStringProperty(currencyFormat.format(cellData.getValue().getGame().getPrice()));
        });

        quantityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()));

        totalColumn.setCellValueFactory(cellData -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            double total = cellData.getValue().getGame().getPrice() * cellData.getValue().getQuantity();
            return new SimpleStringProperty(currencyFormat.format(total));
        });

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setOnAction(event -> {
                    CartItem cartItem = getTableView().getItems().get(getIndex());
                    removeFromCart(cartItem);
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });
    }

    private void loadCartItems() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            return;
        }

        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();

            // Get cart items
            List<Cart> cartList = cartService.getCartByUserId(userId);

            // Get all games in one request
            List<Game> games = gameService.getAllGames();

            // Create a map of game ID to Game object for quick lookup
            for (Game game : games) {
                gameMap.put(game.getId(), game);
            }

            // Clear existing items
            cartItems.clear();
            cartTotal = 0.0;

            // Add cart items to the observable list
            for (Cart cart : cartList) {
                Game game = gameMap.get(cart.getGameId());
                if (game != null) {
                    cartItems.add(new CartItem(game, cart.getQuantity()));
                    cartTotal += game.getPrice() * cart.getQuantity();
                }
            }

            // Update the table and total
            cartTable.setItems(cartItems);
            updateTotalLabel();

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Cart Error", "Could not load cart items: " + e.getMessage());
        }
    }

    private void updateTotalLabel() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        totalPriceLabel.setText(currencyFormat.format(cartTotal));
    }

    private void removeFromCart(CartItem cartItem) {
        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            cartService.removeFromCart(userId, cartItem.getGame().getId());

            // Update UI
            cartItems.remove(cartItem);
            cartTotal -= cartItem.getGame().getPrice() * cartItem.getQuantity();
            updateTotalLabel();

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Cart Error", "Could not remove item from cart: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearCart() {
        if (cartItems.isEmpty()) {
            AlertUtils.showInfoAlert("Cart Empty", "Empty Cart", "Your cart is already empty.");
            return;
        }

        boolean confirmed = AlertUtils.showConfirmationAlert(
                "Clear Cart",
                "Clear Shopping Cart",
                "Are you sure you want to remove all items from your cart?"
        );

        if (confirmed) {
            try {
                int userId = SessionManager.getInstance().getCurrentUser().getId();
                cartService.clearCart(userId);

                // Update UI
                cartItems.clear();
                cartTotal = 0.0;
                updateTotalLabel();

            } catch (IOException e) {
                AlertUtils.showErrorAlert("Error", "Cart Error", "Could not clear cart: " + e.getMessage());
            }
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
            Stage stage = (Stage) cartTable.getScene().getWindow();
            stage.setScene(new Scene(dashboardView));

            // Show the games view
            controller.showGamesView(null);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Navigation Error", "Could not navigate to store: " + e.getMessage());
        }
    }

    @FXML
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            AlertUtils.showWarningAlert("Empty Cart", "Cannot Checkout", "Your cart is empty. Add some games before checkout.");
            return;
        }

        boolean confirmed = AlertUtils.showConfirmationAlert(
                "Checkout",
                "Confirm Purchase",
                "Are you sure you want to complete your purchase? Total: " + totalPriceLabel.getText()
        );

        if (confirmed) {
            try {
                int userId = SessionManager.getInstance().getCurrentUser().getId();
                orderService.createOrderFromCart(userId);

                // Show success message
                AlertUtils.showInfoAlert(
                        "Order Placed",
                        "Purchase Successful",
                        "Your order has been placed successfully. You can view your orders in the My Orders section."
                );

                // Clear cart items after successful checkout
                cartItems.clear();
                cartTotal = 0.0;
                updateTotalLabel();

            } catch (IOException e) {
                AlertUtils.showErrorAlert("Error", "Checkout Error", "Could not complete checkout: " + e.getMessage());
            }
        }
    }

    public static class CartItem {
        private final Game game;
        private final int quantity;

        public CartItem(Game game, int quantity) {
            this.game = game;
            this.quantity = quantity;
        }

        public Game getGame() {
            return game;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
