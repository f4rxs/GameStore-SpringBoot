package com.repositories;

import com.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUserId(int userId);
    List<Review> findByGameId(int gameId);
    List<Review> findByUserIdAndGameId(int userId, int gameId);
}