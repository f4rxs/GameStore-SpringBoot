package com.example.gamestore.services;

import com.example.gamestore.models.Review;
import com.example.gamestore.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findReviewById(int id) {
        return reviewRepository.findById(id);
    }

    public List<Review> findReviewsByUserId(int userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> findReviewsByGameId(int gameId) {
        return reviewRepository.findByGameId(gameId);
    }

    public Optional<Review> findReviewByUserAndGame(int userId, int gameId) {
        List<Review> reviews = reviewRepository.findByUserIdAndGameId(userId, gameId);
        return reviews.isEmpty() ? Optional.empty() : Optional.of(reviews.get(0));
    }

    public Review saveReview(Review review) {
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        return reviewRepository.save(review);
    }

    public void deleteReview(int id) {
        reviewRepository.deleteById(id);
    }

    public double calculateAverageRatingForGame(int gameId) {
        List<Review> reviews = reviewRepository.findByGameId(gameId);
        if (reviews.isEmpty()) {
            return 0;
        }

        int sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }

        return (double) sum / reviews.size();
    }
}