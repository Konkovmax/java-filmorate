package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final UserDbStorage userStorage;


    @Autowired
    public ReviewService(ReviewDbStorage reviewStorage, UserDbStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;

    }

    public Review create(Review review) {
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        review.setGenres(review.getGenres().stream()
                .distinct()
                .collect(Collectors.toList()));
        if (reviewStorage.update(review).isPresent()) {
            log.info("Review updated");
            return reviewStorage.update(review).get();
        } else {
            log.warn("Review not found");
            throw new NotFoundException(String.format(
                    "Review with id: %s not found",
                    review.getId()));
        }
    }

    public List<Review> findAll() {
        return reviewStorage.findAll();
    }

    public Review getReview(int reviewId) {
        if (reviewStorage.getReview(reviewId).isPresent()) {
            return reviewStorage.getReview(reviewId).get();
        } else {
            throw new NotFoundException(String.format(
                    "Review with id: %s not found", reviewId));
        }
    }

    public void addLike(int reviewId, int userId) {
        reviewStorage.addLike(reviewId, userId);
    }

    public void removeLike(int reviewId, int userId) {
        if (userStorage.userExistCheck(userId) == 0) {
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    userId));
        } else {
            reviewStorage.removeLike(reviewId, userId);
        }
    }

    public List<Review> getPopular(int count) {
        return reviewStorage.getPopular(count);
    }
}
