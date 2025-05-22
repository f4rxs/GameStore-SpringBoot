package com.example.gamestoreclient.services;

import com.example.gamestoreclient.models.WishList;
import com.example.gamestoreclient.utils.HttpUtils;
import com.example.gamestoreclient.config.ApiConfig;
import com.google.gson.reflect.TypeToken;



import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


public class WishListService {
    public List<WishList> getAllWishLists() throws IOException {
        Type wishListType = new TypeToken<List<WishList>>(){}.getType();
        return HttpUtils.getList(ApiConfig.WISHLIST_URL, wishListType);
    }

    public List<WishList> getWishListByUserId(int userId) throws IOException {
        Type wishListType = new TypeToken<List<WishList>>(){}.getType();
        return HttpUtils.getList(ApiConfig.WISHLIST_URL + "/user/" + userId, wishListType);
    }

    public WishList getWishListItem(int userId, int gameId) throws IOException {
        return HttpUtils.get(ApiConfig.WISHLIST_URL + "/user/" + userId + "/game/" + gameId, WishList.class);
    }

    public boolean isInWishList(int userId, int gameId) throws IOException {
        return HttpUtils.get(ApiConfig.WISHLIST_URL + "/user/" + userId + "/game/" + gameId + "/check", Boolean.class);
    }

    public WishList addToWishList(WishList wishList) throws IOException {
        return HttpUtils.post(ApiConfig.WISHLIST_URL, wishList, WishList.class);
    }

    public void removeFromWishList(int userId, int gameId) throws IOException {
        HttpUtils.delete(ApiConfig.WISHLIST_URL + "/user/" + userId + "/game/" + gameId);
    }

}
