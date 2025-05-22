package com.example.gamestoreclient.models;

public class OrderItem {
    private int orderId;
    private int gameId;
    private int quantity;
    private double price;

    public OrderItem() {}

    public OrderItem(int orderId, int gameId, int quantity, double price) {
        this.orderId = orderId;
        this.gameId = gameId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
