package com.controllers;

import com.models.Cart;
import com.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Cart>> getCartByUserId(@PathVariable int userId) {
        List<Cart> cartItems = cartService.findCartItemsByUserId(userId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<Cart> getCartItem(@PathVariable int userId, @PathVariable int gameId) {
        Optional<Cart> cartItem = cartService.findCartItem(userId, gameId);
        return cartItem.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cart> addToCart(@RequestBody Cart cart) {
        Cart savedCart = cartService.addToCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCart);
    }

    @PutMapping
    public ResponseEntity<Cart> updateCartItem(@RequestBody Cart cart) {
        Cart updatedCart = cartService.updateCartItem(cart);
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