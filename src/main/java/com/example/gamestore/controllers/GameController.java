package com.example.gamestore.controllers;

import com.example.gamestore.models.Game;
import com.example.gamestore.utils.ErrorResponse;
import com.example.gamestore.services.GameService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping // Tested
    public ResponseEntity<?> getAllGames() {
        try {
            logger.info("Fetching all games");
            List<Game> games = gameService.findAllGames();
            logger.info("Retrieved {} games", games.size());
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            logger.error("Error fetching all games: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch games", 500));
        }
    }

    @GetMapping("/{id}") // Tested
    public ResponseEntity<?> getGameById(@PathVariable int id) {
        try {
            logger.info("Fetching game with id: {}", id);
            Optional<Game> game = gameService.findGameById(id);
            if (game.isPresent()) {
                logger.info("Found game: {}", game.get().getTitle());
                return ResponseEntity.ok(game.get());
            }
            logger.warn("Game not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Game not found", 404));
        } catch (Exception e) {
            logger.error("Error fetching game {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch game", 500));
        }
    }

    @GetMapping("/genre/{genreId}") // Tested
    public ResponseEntity<?> getGamesByGenre(@PathVariable int genreId) {
        try {
            logger.info("Fetching games for genre id: {}", genreId);
            List<Game> games = gameService.findGamesByGenre(genreId);
            logger.info("Found {} games in genre {}", games.size(), genreId);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            logger.error("Error fetching games for genre {}: {}", genreId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch games by genre", 500));
        }
    }

    @GetMapping("/user/{userId}/purchased") // Tested
    public ResponseEntity<?> getPurchasedGamesForUser(@PathVariable int userId) {
        try {
            logger.info("Fetching purchased games for user id: {}", userId);
            List<Game> games = gameService.getPurchasedGamesForUser(userId);
            logger.info("Found {} purchased games for user {}", games.size(), userId);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            logger.error("Error fetching purchased games for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch purchased games", 500));
        }
    }

    @GetMapping("/search") // Tested
    public ResponseEntity<?> searchGames(@RequestParam String title) {
        try {
            logger.info("Searching games with title containing: {}", title);
            List<Game> games = gameService.searchGamesByTitle(title);
            logger.info("Found {} games matching search criteria", games.size());
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            logger.error("Error searching games with title {}: {}", title, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to search games", 500));
        }
    }

    @GetMapping("/price") // Tested
    public ResponseEntity<?> getGamesByMaxPrice(@RequestParam double maxPrice) {
        try {
            logger.info("Fetching games with max price: {}", maxPrice);
            List<Game> games = gameService.findGamesByMaxPrice(maxPrice);
            logger.info("Found {} games within price range", games.size());
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            logger.error("Error fetching games by price {}: {}", maxPrice, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch games by price", 500));
        }
    }

    @GetMapping("/available") // Tested
    public ResponseEntity<?> getAvailableGames() {
        try {
            logger.info("Fetching available games");
            List<Game> games = gameService.findAvailableGames();
            logger.info("Found {} available games", games.size());
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            logger.error("Error fetching available games: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch available games", 500));
        }
    }

    @PostMapping  // Tested
    public ResponseEntity<?> createGame(@RequestBody Game game) {
        try {
            logger.info("Creating new game: {}", game.getTitle());
            Game savedGame = gameService.saveGame(game);
            logger.info("Successfully created game with id: {}", savedGame.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGame);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid game data: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            logger.error("Error creating game: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create game", 500));
        }
    }


    @PutMapping("/{id}") // Tested
    public ResponseEntity<?> updateGame(@PathVariable int id, @RequestBody Game game) {
        try {
            logger.info("Updating game with id: {}", id);
            game.setId(id);
            Game updatedGame = gameService.saveGame(game);
            logger.info("Successfully updated game: {}", updatedGame.getTitle());
            return ResponseEntity.ok(updatedGame);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid game data for update: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            logger.error("Error updating game {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update game", 500));
        }
    }

    @DeleteMapping("/{id}") // Tested
    public ResponseEntity<?> deleteGame(@PathVariable int id) {
        try {
            logger.info("Deleting game with id: {}", id);
            gameService.deleteGame(id);
            logger.info("Successfully deleted game with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting game {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete game", 500));
        }
    }
}