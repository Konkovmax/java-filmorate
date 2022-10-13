package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.Review;

import ru.yandex.practicum.filmorate.storages.EventStorage;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.ReviewStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;


    public Review create(Review review) {
        if (review.getUserId() == 0) {
            log.error("user can't be empty");
            throw new BadRequestException("user can't be empty");
        }
        if (review.getFilmId() == 0) {
            log.error("film can't be empty");
            throw new BadRequestException("film can't be empty");
        }
        if (review.getIsPositive() == null) {
            log.error("score can't be empty");
            throw new BadRequestException("score can't be empty");
        }
        if (userStorage.userExistCheck(review.getUserId()) == 0) {
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    review.getUserId()));
        } else if (filmStorage.getById(review.getFilmId()).isEmpty()) {
            log.warn("Film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found",
                    review.getFilmId()));
        } else {
            Review createdReview = reviewStorage.create(review);
            Event reviewEvent = new Event(createdReview.getUserId(), "REVIEW", "ADD");
            reviewEvent.setEntityId(createdReview.getReviewId());
            eventStorage.create(reviewEvent);
            return createdReview;
        }
    }

    public Review update(Review review) {
        int reviewId = review.getReviewId();
        var updatedReview = reviewStorage.update(review);
        if (updatedReview.isPresent()) {
            int userId = reviewStorage.getById(reviewId).get().getUserId();
            Event reviewEvent = new Event(userId, "REVIEW", "UPDATE");
            reviewEvent.setEntityId(updatedReview.get()
                    .getReviewId());
            eventStorage.create(reviewEvent);

            log.info("Review updated");
            return updatedReview.get();
        } else {
            log.warn("Review not found");
            throw new NotFoundException(String.format(
                    "Review with id: %s not found",
                    review.getReviewId()));
        }
    }

    public Review getById(int reviewId) {
        if (reviewStorage.getById(reviewId).isPresent()) {
            return reviewStorage.getById(reviewId).get();
        } else {
            throw new NotFoundException(String.format(
                    "Review with id: %s not found", reviewId));
        }
    }

    public void addReviewReaction(int reviewId, int userId, boolean isLike) {
        reviewStorage.addReviewReaction(reviewId, userId, isLike);
    }

    public void removeReviewReaction(int reviewId, int userId, boolean isLike) {
        if (userStorage.userExistCheck(userId) == 0) {
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    userId));
        } else {
            reviewStorage.removeReviewReaction(reviewId, userId, isLike);
        }
    }

    private final Comparator<Review> comparator = Comparator.comparingInt(Review::getUseful).reversed();

    public List<Review> getAll(int filmId, int count) {
        List<Review> reviews;
        if (filmId == 0) {
            reviews = reviewStorage.findAll();
        } else {
            reviews = reviewStorage.getFilmReviews(filmId, count);
        }
        Collections.sort(reviews, comparator);
        return reviews;
    }

    public void delete(int reviewId) {
        var reviewToDelete = reviewStorage.getById(reviewId);
        if (reviewToDelete.isEmpty()) {
            throw new NotFoundException(String.format(
                    "Review with id: %s not found", reviewId));
        } else {
            Event reviewEvent = new Event(reviewToDelete.get().getUserId(), "REVIEW",
                    "REMOVE");
            reviewEvent.setEntityId(reviewId);
            eventStorage.create(reviewEvent);
            reviewStorage.delete(reviewId);
        }
    }
}
