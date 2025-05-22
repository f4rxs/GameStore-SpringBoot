package com.example.gamestoreclient.config;

public class ApiConfig {

    private static final String BASE_URL = "http://localhost:8080/api";

    // API endpoints
    public static final String USERS_URL = BASE_URL + "/users";
    public static final String GAMES_URL = BASE_URL + "/games";
    public static final String REVIEWS_URL = BASE_URL + "/reviews";
    public static final String ORDERS_URL = BASE_URL + "/orders";
    public static final String ORDER_ITEMS_URL = BASE_URL + "/orderitems";
    public static final String GENRES_URL = BASE_URL + "/genres";
    public static final String WISHLIST_URL = BASE_URL + "/wishlist";
    public static final String CART_URL = BASE_URL + "/cart";

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
