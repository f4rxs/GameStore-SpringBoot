package com.example.gamestoreclient.services;

import com.example.gamestoreclient.models.Genre;
import com.example.gamestoreclient.config.ApiConfig;
import com.example.gamestoreclient.utils.HttpUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class GenreService {

    public List<Genre> getAllGenres() throws IOException {
        TypeReference<List<Genre>> genreListType = new TypeReference<List<Genre>>() {};
        return HttpUtils.getList(ApiConfig.GENRES_URL, genreListType);
    }

    public Genre getGenreById(int id) throws IOException {
        return HttpUtils.get(ApiConfig.GENRES_URL + "/" + id, Genre.class);
    }

    public Genre getGenreByGameId(int gameId) throws IOException {
        return HttpUtils.get(ApiConfig.GENRES_URL + "/game/" + gameId, Genre.class);
    }

    public Genre getGenreByName(String name) throws IOException {
        return HttpUtils.get(ApiConfig.GENRES_URL + "/name/" + name, Genre.class);
    }

    public Genre createGenre(Genre genre) throws IOException {
        return HttpUtils.post(ApiConfig.GENRES_URL, genre, Genre.class);
    }

    public Genre updateGenre(Genre genre) throws IOException {
        return HttpUtils.put(ApiConfig.GENRES_URL + "/" + genre.getId(), genre, Genre.class);
    }

    public void deleteGenre(int id) throws IOException {
        HttpUtils.delete(ApiConfig.GENRES_URL + "/" + id);
    }
}
