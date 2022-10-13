package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.storages.EventDbStorage;
import ru.yandex.practicum.filmorate.storages.FilmDbStorage;
import ru.yandex.practicum.filmorate.storages.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storages.UserDbStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final EventDbStorage eventStorage;

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
        } else if (filmStorage.getFilm(review.getFilmId()).isEmpty()) {
            log.warn("Film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found",
                    review.getFilmId()));
        } else {
            Review createdReview = reviewStorage.create(review);
            Event reviewEvent = new Event(createdReview.getUserId(), "REVIEW", "ADD");
            reviewEvent.setEntityId(createdReview.getReviewId());
            eventStorage.addEvent(reviewEvent);
            return createdReview;
        }
    }

    public Review update(Review review) {
        int reviewId = review.getReviewId();
        var updatedReview = reviewStorage.update(review);
        if (updatedReview.isPresent()) {
            int userId = reviewStorage.getReview(reviewId).get().getUserId();
            Event reviewEvent = new Event(userId, "REVIEW", "UPDATE");
            reviewEvent.setEntityId(updatedReview.get()
                    .getReviewId());
            eventStorage.addEvent(reviewEvent);

            log.info("Review updated");
            return updatedReview.get();
        } else {
            log.warn("Review not found");
            throw new NotFoundException(String.format(
                    "Review with id: %s not found",
                    review.getReviewId()));
        }
    }

    public Review getReview(int reviewId) {
        return reviewStorage.getReview(reviewId)
                .orElseThrow(() -> new NotFoundException("Not found review with id: " + reviewId));

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

    public List<Review> getAllReviews(int filmId, int count) {
        List<Review> reviews;
        if (filmId == 0) {
            reviews = reviewStorage.findAllReviews();
        } else {
            reviews = reviewStorage.getFilmReviews(filmId, count);
        }
        Collections.sort(reviews, comparator);
        return reviews;
    }

    public void removeReview(int reviewId) {
        var reviewToDelete = reviewStorage.getReview(reviewId);
        if (reviewToDelete.isEmpty()) {
            throw new NotFoundException(String.format(
                    "Review with id: %s not found", reviewId));
        } else {
            Event reviewEvent = new Event(reviewToDelete.get().getUserId(), "REVIEW",
                    "REMOVE");
            reviewEvent.setEntityId(reviewId);
            eventStorage.addEvent(reviewEvent);

            reviewStorage.removeReview(reviewId);
        }
    }

    private final Comparator<Review> comparator = Comparator.comparingInt(Review::getUseful).reversed();
}
