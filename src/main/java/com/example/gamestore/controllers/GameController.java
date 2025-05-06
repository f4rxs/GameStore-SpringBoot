package com.example.gamestore.controllers;

import com.example.gamestore.models.Game;
import com.example.gamestore.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.findAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable int id) {
        Optional<Game> game = gameService.findGameById(id);
        return game.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<Game>> getGamesByGenre(@PathVariable int genreId) {
        List<Game> games = gameService.findGamesByGenre(genreId);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/user/{userId}/purchased")
    public ResponseEntity<List<Game>> getPurchasedGamesForUser(@PathVariable int userId) {
        try {
            List<Game> games = gameService.getPurchasedGamesForUser(userId);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Game>> searchGames(@RequestParam String title) {
        List<Game> games = gameService.searchGamesByTitle(title);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/price")
    public ResponseEntity<List<Game>> getGamesByMaxPrice(@RequestParam double maxPrice) {
        List<Game> games = gameService.findGamesByMaxPrice(maxPrice);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Game>> getAvailableGames() {
        List<Game> games = gameService.findAvailableGames();
        return ResponseEntity.ok(games);
    }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game savedGame = gameService.saveGame(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGame);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable int id, @RequestBody Game game) {
        game.setId(id);
        Game updatedGame = gameService.saveGame(game);
        return ResponseEntity.ok(updatedGame);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable int id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}