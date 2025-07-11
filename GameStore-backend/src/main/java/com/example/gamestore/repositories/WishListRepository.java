package com.example.gamestore.repositories;

import com.example.gamestore.models.WishList;
import com.example.gamestore.models.WishList.WishListId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, WishListId> {
    List<WishList> findByIdUserId(int userId);
    void deleteByIdUserIdAndIdGameId(int userId, int gameId);
}