package com.services;

import com.models.Cart;
import com.models.Cart.CartId;
import com.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<Cart> findAllCartItems() {
        return cartRepository.findAll();
    }

    public List<Cart> findCartItemsByUserId(int userId) {
        return cartRepository.findByIdUserId(userId);
    }

    public Optional<Cart> findCartItem(int userId, int gameId) {
        CartId cartId = new CartId(userId, gameId);
        return cartRepository.findById(cartId);
    }

    public Cart addToCart(Cart cart) {
        if (cart.getAddedAt() == null) {
            cart.setAddedAt(new Timestamp(System.currentTimeMillis()));
        }
        return cartRepository.save(cart);
    }

    public Cart updateCartItem(Cart cart) {
        return cartRepository.save(cart);
    }

    @Transactional
    public void removeFromCart(int userId, int gameId) {
        cartRepository.deleteByIdUserIdAndIdGameId(userId, gameId);
    }

    @Transactional
    public void clearCart(int userId) {
        cartRepository.deleteByIdUserId(userId);
    }
}