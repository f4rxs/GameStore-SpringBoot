package com.example.gamestore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Column(name = "genre_id")
    private int genreId;

    private double price;



    @Column(name = "release_date")
    private Date releaseDate;

    private int stock;

    private String description;



    public Game() {}

    public Game(int id, String title, int genreId, double price, Date releaseDate, int stock, String description) {
        this.id = id;
        this.title = title;
        this.genreId = genreId;
        this.price = price;
        this.releaseDate = releaseDate;
        this.stock = stock;
        this.description = description;
    }

    // Getters and setters remain the same
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getGenreId() { return genreId; }
    public void setGenreId(int genreId) { this.genreId = genreId; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }


    public Date getReleaseDate() { return releaseDate; }
    public void setRelaeaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }



    @Override
    public String toString() {
        return "Game{id=" + id + ", title='" + title + "', price=" + price + ", stock=" + stock + " description=" + description + "}";
    }
}
