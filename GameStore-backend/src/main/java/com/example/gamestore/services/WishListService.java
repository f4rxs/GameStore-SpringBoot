package com.example.gamestore.services;

import com.example.gamestore.models.WishList;
import com.example.gamestore.models.WishList.WishListId;
import com.example.gamestore.repositories.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class WishListService {

    private final WishListRepository wishListRepository;

    @Autowired
    public WishListService(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    public List<WishList> findAllWishListItems() {
        return wishListRepository.findAll();
    }

    public List<WishList> findWishListByUserId(int userId) {
        return wishListRepository.findByIdUserId(userId);
    }

    public Optional<WishList> findWishListItem(int userId, int gameId) {
        WishListId wishListId = new WishListId(userId, gameId);
        return wishListRepository.findById(wishListId);
    }

    public WishList addToWishList(WishList wishList) {
        if (wishList.getAddedAt() == null) {
            wishList.setAddedAt(new Timestamp(System.currentTimeMillis()));
        }
        return wishListRepository.save(wishList);
    }

    @Transactional
    public void removeFromWishList(int userId, int gameId) {
        wishListRepository.deleteByIdUserIdAndIdGameId(userId, gameId);
    }

    public boolean isInWishList(int userId, int gameId) {
        WishListId wishListId = new WishListId(userId, gameId);
        return wishListRepository.existsById(wishListId);
    }
}