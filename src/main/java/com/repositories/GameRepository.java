package com.repositories;

import com.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    List<Game> findByGenreId(int genreId);
    List<Game> findByTitleContainingIgnoreCase(String title);
    List<Game> findByPriceLessThanEqual(double price);
    List<Game> findByStockGreaterThan(int stock);
}