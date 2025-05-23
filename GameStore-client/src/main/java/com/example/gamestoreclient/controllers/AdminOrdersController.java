package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.models.Order;
import com.example.gamestoreclient.models.OrderItem;
import com.example.gamestoreclient.models.User;
import com.example.gamestoreclient.services.GameService;
import com.example.gamestoreclient.services.OrderService;
import com.example.gamestoreclient.services.UserService;
import com.example.gamestoreclient.utils.AlertUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminOrdersController  implements  Initializable {

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, String> userColumn;

    @FXML
    private TableColumn<Order, String> dateColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TableColumn<Order, String> totalColumn;

    @FXML
    private TableColumn<Order, HBox> actionsColumn;

    @FXML
    private VBox orderDetailsContainer;

    @FXML
    private TableView<OrderItemDetails> orderItemsTable;

    @FXML
    private TableColumn<OrderItemDetails, String> gameColumn;

    @FXML
    private TableColumn<OrderItemDetails, String> priceColumn;

    @FXML
    private TableColumn<OrderItemDetails, Integer> quantityColumn;

    @FXML
    private TableColumn<OrderItemDetails, String> itemTotalColumn;

    private final OrderService orderService = new OrderService();
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();

    private ObservableList<Order> orders = FXCollections.observableArrayList();
    private ObservableList<OrderItemDetails> orderItems = FXCollections.observableArrayList();
    private Map<Integer, User> userMap = new HashMap<>();
    private Map<Integer, Game> gameMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupStatusFilter();
        setupOrdersTable();
        setupOrderItemsTable();
        loadUsers();
        loadGames();
        loadOrders();
    }

    private void setupStatusFilter() {
        statusFilter.getItems().addAll("All", "PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");
        statusFilter.setValue("All");

        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if ("All".equals(newVal)) {
                    loadOrders();
                } else {
                    loadOrdersByStatus(newVal);
                }
            }
        });
    }

    private void setupOrdersTable() {
        orderIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        userColumn.setCellValueFactory(cellData -> {
            User user = userMap.get(cellData.getValue().getUserId());
            return new SimpleStringProperty(user != null ? user.getName() : "Unknown");
        });

        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            return new SimpleStringProperty(dateFormat.format(cellData.getValue().getOrderDate()));
        });

        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        totalColumn.setCellValueFactory(cellData -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return new SimpleStringProperty(currencyFormat.format(cellData.getValue().getTotalAmount()));
        });

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button viewDetailsButton = new Button("View Details");
            private final ComboBox<String> statusComboBox = new ComboBox<>();
            private final HBox controlBox = new HBox(10, viewDetailsButton, statusComboBox);

            {
                statusComboBox.getItems().addAll("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");

                viewDetailsButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order);
                });

                statusComboBox.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    String newStatus = statusComboBox.getValue();
                    if (newStatus != null && !newStatus.equals(order.getStatus())) {
                        updateOrderStatus(order, newStatus);
                    }
                });
            }

            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    statusComboBox.setValue(order.getStatus());
                    setGraphic(controlBox);
                }
            }
        });
    }

    private void setupOrderItemsTable() {
        gameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getTitle()));

        priceColumn.setCellValueFactory(cellData -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return new SimpleStringProperty(currencyFormat.format(cellData.getValue().getPrice()));
        });

        quantityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()));

        itemTotalColumn.setCellValueFactory(cellData -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            double total = cellData.getValue().getPrice() * cellData.getValue().getQuantity();
            return new SimpleStringProperty(currencyFormat.format(total));
        });
    }

    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();

            // Create a map of user ID to User object for quick lookup
            for (User user : users) {
                userMap.put(user.getId(), user);
            }

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load users: " + e.getMessage());
        }
    }

    private void loadGames() {
        try {
            List<Game> games = gameService.getAllGames();

            // Create a map of game ID to Game object for quick lookup
            for (Game game : games) {
                gameMap.put(game.getId(), game);
            }

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load games: " + e.getMessage());
        }
    }

    private void loadOrders() {
        try {
            List<Order> allOrders = orderService.getAllOrders();

            orders.clear();
            orders.addAll(allOrders);
            ordersTable.setItems(orders);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load orders: " + e.getMessage());
        }
    }

    private void loadOrdersByStatus(String status) {
        try {
            List<Order> filteredOrders = orderService.getOrdersByStatus(status);

            orders.clear();
            orders.addAll(filteredOrders);
            ordersTable.setItems(orders);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Data Loading Error", "Could not load orders by status: " + e.getMessage());
        }
    }

    private void showOrderDetails(Order order) {
        try {
            List<OrderItem> items = orderService.getOrderItems(order.getId());

            orderItems.clear();

            for (OrderItem item : items) {
                Game game = gameMap.get(item.getGameId());
                if (game != null) {
                    orderItems.add(new OrderItemDetails(game, item.getQuantity(), item.getPrice()));
                }
            }

            orderItemsTable.setItems(orderItems);
            orderDetailsContainer.setVisible(true);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Order Details Error", "Could not load order details: " + e.getMessage());
        }
    }

    private void updateOrderStatus(Order order, String newStatus) {
        try {
            orderService.updateOrderStatus(order.getId(), newStatus);

            // Update the order in the list
            order.setStatus(newStatus);

            // Refresh the table
            ordersTable.refresh();

            AlertUtils.showInfoAlert("Success", "Status Updated",
                    "Order #" + order.getId() + " status has been updated to " + newStatus + ".");

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Update Error", "Could not update order status: " + e.getMessage());
        }
    }

    // Helper class to represent an order item with game details
    public static class OrderItemDetails {
        private final Game game;
        private final int quantity;
        private final double price;

        public OrderItemDetails(Game game, int quantity, double price) {
            this.game = game;
            this.quantity = quantity;
            this.price = price;
        }

        public Game getGame() {
            return game;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }
    }
}
