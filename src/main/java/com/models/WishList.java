package com.models;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "wishlist")
public class WishList {
    @EmbeddedId
    private WishListId id;

    @Column(name = "added_at")
    private Timestamp addedAt;

    public WishList() {}

    public WishList(int userId, int gameId, Timestamp addedAt) {
        this.id = new WishListId(userId, gameId);
        this.addedAt = addedAt;
    }

    // Composite key class
    @Embeddable
    public static class WishListId implements java.io.Serializable {
        @Column(name = "user_id")
        private int userId;

        @Column(name = "game_id")
        private int gameId;

        public WishListId() {}

        public WishListId(int userId, int gameId) {
            this.userId = userId;
            this.gameId = gameId;
        }

        // equals and hashCode methods
    }

    // Getters and setters
    public int getUserId() {
        return id.userId;
    }

    public void setUserId(int userId) {
        if (this.id == null) {
            this.id = new WishListId();
        }
        this.id.userId = userId;
    }

    public int getGameId() {
        return id.gameId;
    }

    public void setGameId(int gameId) {
        if (this.id == null) {
            this.id = new WishListId();
        }
        this.id.gameId = gameId;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "WishList{userId=" + id.userId + ", gameId=" + id.gameId + ", addedAt=" + addedAt + "}";
    }

}
