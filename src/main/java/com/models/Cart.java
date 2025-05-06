package com.models;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "cart")
public class Cart {

    @EmbeddedId
    private CartId id;

    private int quantity;

    @Column(name = "added_at")
    private Timestamp addedAt;

    public Cart() {}

    public Cart(int userId, int gameId, int quantity, Timestamp addedAt) {
        this.id = new CartId(userId, gameId);
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    // Composite key class
    @Embeddable
    public static class CartId implements java.io.Serializable {
        @Column(name = "user_id")
        private int userId;

        @Column(name = "game_id")
        private int gameId;

        public CartId() {}

        public CartId(int userId, int gameId) {
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
            this.id = new CartId();
        }
        this.id.userId = userId;
    }

    public int getGameId() {
        return id.gameId;
    }

    public void setGameId(int gameId) {
        if (this.id == null) {
            this.id = new CartId();
        }
        this.id.gameId = gameId;
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
