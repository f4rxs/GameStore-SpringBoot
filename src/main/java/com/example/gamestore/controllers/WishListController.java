package com.example.gamestore.controllers;

import com.example.gamestore.models.WishList;
import com.example.gamestore.services.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/wishlist")
public class WishListController {

    private final WishListService wishListService;

    @Autowired
    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @GetMapping
    public ResponseEntity<List<WishList>> getAllWishLists() {
        List<WishList> wishListItems = wishListService.findAllWishListItems();
        return ResponseEntity.ok(wishListItems);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WishList>> getWishListByUserId(@PathVariable int userId) {
        List<WishList> wishListItems = wishListService.findWishListByUserId(userId);
        return ResponseEntity.ok(wishListItems);
    }

    @GetMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<WishList> getWishListItem(@PathVariable int userId, @PathVariable int gameId) {
        Optional<WishList> wishListItem = wishListService.findWishListItem(userId, gameId);
        return wishListItem.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/game/{gameId}/check")
    public ResponseEntity<Boolean> isInWishList(@PathVariable int userId, @PathVariable int gameId) {
        boolean isInWishList = wishListService.isInWishList(userId, gameId);
        return ResponseEntity.ok(isInWishList);
    }

    @PostMapping
    public ResponseEntity<WishList> addToWishList(@RequestBody WishList wishList) {
        WishList savedWishList = wishListService.addToWishList(wishList);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWishList);
    }

    @DeleteMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<Void> removeFromWishList(@PathVariable int userId, @PathVariable int gameId) {
        wishListService.removeFromWishList(userId, gameId);
        return ResponseEntity.noContent().build();
    }
}