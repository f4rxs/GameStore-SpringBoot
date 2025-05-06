package com.example.gamestore.controllers;

import com.example.gamestore.models.Cart;
import com.example.gamestore.models.Game;
import com.example.gamestore.services.CartService;
import com.example.gamestore.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final GameService gameService;

    @Autowired
    public CartController(CartService cartService, GameService gameService) {
        this.cartService = cartService;
        this.gameService = gameService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Cart>> getCartByUserId(@PathVariable int userId) {
        List<Cart> cartItems = cartService.findCartItemsByUserId(userId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/user/{userId}/games")
    public ResponseEntity<List<Game>> getUserCartGames(@PathVariable int userId) {
        List<Cart> cartItems = cartService.findCartItemsByUserId(userId);
        List<Game> games = new ArrayList<>();

        for (Cart item : cartItems) {
            Optional<Game> gameOpt = gameService.findGameById(item.getGameId());
            gameOpt.ifPresent(games::add);
        }

        return ResponseEntity.ok(games);
    }

    @GetMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<Cart> getCartItem(@PathVariable int userId, @PathVariable int gameId) {
        Optional<Cart> cartItem = cartService.findCartItem(userId, gameId);
        return cartItem.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cart> addToCart(@RequestBody Cart cart) {
        if (cart.getAddedAt() == null) {
            cart.setAddedAt(new Timestamp(System.currentTimeMillis()));
        }
        Cart savedCart = cartService.addToCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCart);
    }

    @PostMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<Cart> addToCartWithDefaultQuantity(@PathVariable int userId, @PathVariable int gameId) {
        Cart cart = new Cart(userId, gameId, 1, new Timestamp(System.currentTimeMillis()));
        Cart savedCart = cartService.addToCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCart);
    }

    @PutMapping
    public ResponseEntity<Cart> updateCartItem(@RequestBody Cart cart) {
        Cart updatedCart = cartService.updateCartItem(cart);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/user/{userId}/game/{gameId}/quantity/{quantity}")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @PathVariable int userId,
            @PathVariable int gameId,
            @PathVariable int quantity) {

        Optional<Cart> cartItemOpt = cartService.findCartItem(userId, gameId);
        if (cartItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cart cartItem = cartItemOpt.get();
        cartItem.setQuantity(quantity);
        Cart updatedCart = cartService.updateCartItem(cartItem);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable int userId, @PathVariable int gameId) {
        cartService.removeFromCart(userId, gameId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable int userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}