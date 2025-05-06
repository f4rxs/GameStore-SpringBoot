package com.example.gamestore.services;

import com.example.gamestore.models.Genre;
import com.example.gamestore.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> findAllGenres() {
        return genreRepository.findAll();
    }

    public Optional<Genre> findGenreById(int id) {
        return genreRepository.findById(id);
    }

    public Optional<Genre> findGenreByGameId(int gameId) {
        return genreRepository.findByGameId(gameId);
    }

    public Optional<Genre> findGenreByName(String name) {
        return genreRepository.findByNameIgnoreCase(name);
    }

    public Genre saveGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public void deleteGenre(int id) {
        genreRepository.deleteById(id);
    }
}