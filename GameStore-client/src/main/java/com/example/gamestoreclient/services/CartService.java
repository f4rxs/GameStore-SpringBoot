package com.example.gamestoreclient.services;

import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.models.Cart;
import com.example.gamestoreclient.config.ApiConfig;
import com.example.gamestoreclient.utils.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;



public class CartService {
    public List<Cart> getCartByUserId(int userId) throws IOException {
        Type cartListType = new TypeToken<List<Cart>>(){}.getType();
        return HttpUtils.getList(ApiConfig.CART_URL + "/user/" + userId, cartListType);
    }

    public List<Game> getUserCartGames(int userId) throws IOException {
        Type gameListType = new TypeToken<List<Game>>(){}.getType();
        return HttpUtils.getList(ApiConfig.CART_URL + "/user/" + userId + "/games", gameListType);
    }

    public Cart getCartItem(int userId, int gameId) throws IOException {
        return HttpUtils.get(ApiConfig.CART_URL + "/user/" + userId + "/game/" + gameId, Cart.class);
    }

    public Cart addToCart(Cart cart) throws IOException {
        return HttpUtils.post(ApiConfig.CART_URL, cart, Cart.class);
    }

    public Cart addToCartWithDefaultQuantity(int userId, int gameId) throws IOException {
        return HttpUtils.post(ApiConfig.CART_URL + "/user/" + userId + "/game/" + gameId, null, Cart.class);
    }

    public Cart updateCartItem(Cart cart) throws IOException {
        return HttpUtils.put(ApiConfig.CART_URL, cart, Cart.class);
    }

    public Cart updateCartItemQuantity(int userId, int gameId, int quantity) throws IOException {
        return HttpUtils.put(
                ApiConfig.CART_URL + "/user/" + userId + "/game/" + gameId + "/quantity/" + quantity,
                null,
                Cart.class
        );
    }

    public void removeFromCart(int userId, int gameId) throws IOException {
        HttpUtils.delete(ApiConfig.CART_URL + "/user/" + userId + "/game/" + gameId);
    }

    public void clearCart(int userId) throws IOException {
        HttpUtils.delete(ApiConfig.CART_URL + "/user/" + userId);
    }
}
