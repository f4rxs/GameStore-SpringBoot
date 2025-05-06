package com.services;

import com.models.Game;
import com.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> findGameById(int id) {
        return gameRepository.findById(id);
    }

    public List<Game> findGamesByGenre(int genreId) {
        return gameRepository.findByGenreId(genreId);
    }

    public List<Game> searchGamesByTitle(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Game> findGamesByMaxPrice(double maxPrice) {
        return gameRepository.findByPriceLessThanEqual(maxPrice);
    }

    public List<Game> findAvailableGames() {
        return gameRepository.findByStockGreaterThan(0);
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    public void deleteGame(int id) {
        gameRepository.deleteById(id);
    }

    public boolean updateGameStock(int gameId, int quantity) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isPresent()) {
            Game game = gameOpt.get();
            if (game.getStock() >= quantity) {
                game.setStock(game.getStock() - quantity);
                gameRepository.save(game);
                return true;
            }
        }
        return false;
    }
}