package com.example.gamestoreclient.controllers;

import com.example.gamestoreclient.models.Game;
import com.example.gamestoreclient.models.Review;
import com.example.gamestoreclient.services.ReviewService;
import com.example.gamestoreclient.utils.AlertUtils;
import com.example.gamestoreclient.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.Timestamp;

public class WriteReviewController {

    @FXML
    private Label gameNameLabel;

    @FXML
    private RadioButton rating1;

    @FXML
    private RadioButton rating2;

    @FXML
    private RadioButton rating3;

    @FXML
    private RadioButton rating4;

    @FXML
    private RadioButton rating5;

    @FXML
    private ToggleGroup ratingGroup;

    @FXML
    private TextArea commentArea;

    private Game game;
    private Runnable onReviewSubmitted;
    private final ReviewService reviewService = new ReviewService();

    public void setGame(Game game) {
        this.game = game;
        gameNameLabel.setText("Write a review for " + game.getTitle());
    }

    public void setOnReviewSubmitted(Runnable callback) {
        this.onReviewSubmitted = callback;
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        try {
            // Get selected rating
            RadioButton selectedRating = (RadioButton) ratingGroup.getSelectedToggle();
            int rating = Integer.parseInt(selectedRating.getText());

            // Create review object
            Review review = new Review();
            review.setUserId(SessionManager.getInstance().getCurrentUser().getId());
            review.setGameId(game.getId());
            review.setRating(rating);
            review.setComment(commentArea.getText().trim());
            review.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Submit review
            reviewService.createReview(review);

            // Show success message
            AlertUtils.showInfoAlert("Success", "Review Submitted", "Your review has been submitted successfully.");

            // Call callback if provided
            if (onReviewSubmitted != null) {
                onReviewSubmitted.run();
            }

            // Close the window
            closeWindow(event);

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error", "Review Error", "Could not submit review: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private boolean validateInput() {
        if (ratingGroup.getSelectedToggle() == null) {
            AlertUtils.showWarningAlert("Validation Error", "Rating Required", "Please select a rating.");
            return false;
        }

        if (commentArea.getText().trim().isEmpty()) {
            AlertUtils.showWarningAlert("Validation Error", "Comment Required", "Please write a comment for your review.");
            return false;
        }

        return true;
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
