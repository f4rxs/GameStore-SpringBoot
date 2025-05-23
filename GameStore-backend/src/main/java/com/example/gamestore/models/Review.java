package com.example.gamestore.models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "game_id")
    private int gameId;

    private int rating;

    private String comment;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public Review() {
    }

    public Review(int id, int userId, int gameId, int rating, String comment ,Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.gameId = gameId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and setters remain the same
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Review{id=" + id +
                ", userId=" + userId +
                ", gameId=" + gameId +
                ", rating=" + rating +
                ", comment='" + comment + "'" +
                ", createdAt=" + createdAt + "}";
    }
}
