package com.example.gamestoreclient.services;
import com.example.gamestoreclient.config.ApiConfig;
import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.utils.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
public class GameService {
    public List<Game> getAllGames() throws IOException {
        Type gameListType = new TypeToken<List<Game>>(){}.getType();
        return HttpUtils.getList(ApiConfig.GAMES_URL, gameListType);
    }

    public Game getGameById(int id) throws IOException {
        return HttpUtils.get(ApiConfig.GAMES_URL + "/" + id, Game.class);
    }

    public List<Game> getGamesByGenre(int genreId) throws IOException {
        Type gameListType = new TypeToken<List<Game>>(){}.getType();
        return HttpUtils.getList(ApiConfig.GAMES_URL + "/genre/" + genreId, gameListType);
    }

    public List<Game> getPurchasedGamesForUser(int userId) throws IOException {
        Type gameListType = new TypeToken<List<Game>>(){}.getType();
        return HttpUtils.getList(ApiConfig.GAMES_URL + "/user/" + userId + "/purchased", gameListType);
    }

    public List<Game> searchGamesByTitle(String title) throws IOException {
        Type gameListType = new TypeToken<List<Game>>(){}.getType();
        return HttpUtils.getList(ApiConfig.GAMES_URL + "/search?title=" + title, gameListType);
    }

    public List<Game> getGamesByMaxPrice(double maxPrice) throws IOException {
        Type gameListType = new TypeToken<List<Game>>(){}.getType();
        return HttpUtils.getList(ApiConfig.GAMES_URL + "/price?maxPrice=" + maxPrice, gameListType);
    }

    public List<Game> getAvailableGames() throws IOException {
        Type gameListType = new TypeToken<List<Game>>(){}.getType();
        return HttpUtils.getList(ApiConfig.GAMES_URL + "/available", gameListType);
    }

    public Game createGame(Game game) throws IOException {
        return HttpUtils.post(ApiConfig.GAMES_URL, game, Game.class);
    }

    public Game updateGame(Game game) throws IOException {
        return HttpUtils.put(ApiConfig.GAMES_URL + "/" + game.getId(), game, Game.class);
    }

    public void deleteGame(int id) throws IOException {
        HttpUtils.delete(ApiConfig.GAMES_URL + "/" + id);
    }
}
