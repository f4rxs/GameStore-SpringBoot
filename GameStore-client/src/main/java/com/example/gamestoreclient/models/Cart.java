package com.example.gamestoreclient.models;
import java.sql.Timestamp;
public class Cart {

    private int userId;
    private int gameId;
    private int quantity;
    private Timestamp addedAt;

    public Cart() {}

    public Cart(int userId, int gameId, int quantity, Timestamp addedAt) {
        this.userId = userId;
        this.gameId = gameId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }
}
