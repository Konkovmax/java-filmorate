package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping(value = "/reviews")
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping(value = "/reviews")
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/reviews/{id}")
    public void delete(@PathVariable("id") Integer reviewId) {
        reviewService.removeReview(reviewId);
    }

    @GetMapping("/reviews/{id}")

    public Review getReview(@PathVariable("id") Integer reviewId) {
        return reviewService.get(reviewId);

    }

    @GetMapping("/reviews")
    public List<Review> getAll(
            @RequestParam(value = "filmId", defaultValue = "0", required = false) Integer filmId,
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping(value = "/reviews/{id}/like/{userId}")
    public void addReviewLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.addReviewReaction(reviewId, userId, true);
    }

    @DeleteMapping(value = "/reviews/{id}/like/{userId}")
    public void removeReviewLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.removeReviewReaction(reviewId, userId, true);
    }

    @PutMapping(value = "/reviews/{id}/dislike/{userId}")
    public void addReviewDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.addReviewReaction(reviewId, userId, false);
    }

    @DeleteMapping(value = "/reviews/{id}/dislike/{userId}")
    public void removeReviewDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.removeReviewReaction(reviewId, userId, false);
    }

}
