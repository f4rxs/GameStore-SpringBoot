package com.example.gamestoreclient.models;

import java.sql.Timestamp;
public class Game {

    private int id;
    private String title;
    private String description;
    private double price;
    private int genreId;
    private String imageUrl;
//    private boolean available;
    private Timestamp releaseDate;

    public Game() {}

    public Game(int id, String title, String description, double price, int genreId,
                String imageUrl, Timestamp releaseDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.genreId = genreId;
        this.imageUrl = imageUrl;
//        this.available = available;
        this.releaseDate = releaseDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

//    public boolean isAvailable() {
//        return available;
//    }
//
//    public void setAvailable(boolean available) {
//        this.available = available;
//    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Timestamp releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return title;
    }
}
