package com.example.gamestoreclient.models;

import java.sql.Timestamp;

public class WishList {
    private int userId;
    private int gameId;
    private Timestamp addedAt;

    public WishList() {}

    public WishList(int userId, int gameId, Timestamp addedAt) {
        this.userId = userId;
        this.gameId = gameId;
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

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }
}
