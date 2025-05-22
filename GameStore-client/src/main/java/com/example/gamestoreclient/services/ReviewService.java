package com.example.gamestoreclient.services;

import com.example.gamestoreclient.models.Review;
import com.example.gamestoreclient.utils.HttpUtils;
import com.example.gamestoreclient.config.ApiConfig;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ReviewService {
    public List<Review> getAllReviews() throws IOException {
        Type reviewListType = new TypeToken<List<Review>>(){}.getType();
        return HttpUtils.getList(ApiConfig.REVIEWS_URL, reviewListType);
    }

    public Review getReviewById(int id) throws IOException {
        return HttpUtils.get(ApiConfig.REVIEWS_URL + "/" + id, Review.class);
    }

    public List<Review> getReviewsByUserId(int userId) throws IOException {
        Type reviewListType = new TypeToken<List<Review>>(){}.getType();
        return HttpUtils.getList(ApiConfig.REVIEWS_URL + "/user/" + userId, reviewListType);
    }

    public List<Review> getReviewsByGameId(int gameId) throws IOException {
        Type reviewListType = new TypeToken<List<Review>>(){}.getType();
        return HttpUtils.getList(ApiConfig.REVIEWS_URL + "/game/" + gameId, reviewListType);
    }

    public Review getReviewByUserAndGame(int userId, int gameId) throws IOException {
        return HttpUtils.get(ApiConfig.REVIEWS_URL + "/user/" + userId + "/game/" + gameId, Review.class);
    }

    public double getAverageRatingForGame(int gameId) throws IOException {
        return HttpUtils.get(ApiConfig.REVIEWS_URL + "/game/" + gameId + "/rating", Double.class);
    }

    public Review createReview(Review review) throws IOException {
        return HttpUtils.post(ApiConfig.REVIEWS_URL, review, Review.class);
    }

    public Review updateReview(Review review) throws IOException {
        return HttpUtils.put(ApiConfig.REVIEWS_URL + "/" + review.getId(), review, Review.class);
    }

    public void deleteReview(int id) throws IOException {
        HttpUtils.delete(ApiConfig.REVIEWS_URL + "/" + id);
    }
}
