package com.example.gamestore.repositories;

import com.example.gamestore.models.Cart;
import com.example.gamestore.models.Cart.CartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartId> {
    List<Cart> findByIdUserId(int userId);
    void deleteByIdUserId(int userId);
    void deleteByIdUserIdAndIdGameId(int userId, int gameId);
}
