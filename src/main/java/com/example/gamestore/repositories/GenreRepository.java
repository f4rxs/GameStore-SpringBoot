package com.example.gamestore.repositories;

import com.example.gamestore.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Optional<Genre> findByNameIgnoreCase(String name);
    @Query("SELECT g FROM Genre g JOIN Game gm ON g.id = gm.genreId WHERE gm.id = :gameId")
    Optional<Genre> findByGameId(@Param("gameId") int gameId);
}