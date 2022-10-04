package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
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
    private final FilmDbStorage filmStorage;


    @Autowired
    public ReviewService(ReviewDbStorage reviewStorage, UserDbStorage userStorage, FilmDbStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;

    }

    public Review create(Review review){
        if (review.getUserId()==0) {
            log.error("user can't be empty");
            throw new BadRequestException("user can't be empty");
        }
        if (review.getFilmId()==0) {
            log.error("film can't be empty");
            throw new BadRequestException("film can't be empty");
        }
        if (review.getIsPositive()==null) {
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
        return reviewStorage.create(review);
        }
    }

   public Review update(Review review) {
        if (reviewStorage.update(review).isPresent()) {
            log.info("Review updated");
            return reviewStorage.update(review).get();
        } else {
            log.warn("Review not found");
            throw new NotFoundException(String.format(
                    "Review with id: %s not found",
                    review.getReviewId()));
        }
    }

//    public List<Review> findAll() {
//        return reviewStorage.findAll();
//    }

    public Review getReview(int reviewId) {
        if (reviewStorage.getReview(reviewId).isPresent()) {
            return reviewStorage.getReview(reviewId).get();
        } else {
            throw new NotFoundException(String.format(
                    "Review with id: %s not found", reviewId));
        }
    }

    public void addReviewReaction(int reviewId, int userId, boolean isLike) {
        reviewStorage.addReviewReaction(reviewId, userId, isLike);
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

    public List<Review> getAllReviews(int filmId, int count) {
        if(filmId==0){
            return reviewStorage.findAllReviews();
        }else {
            return reviewStorage.getFilmReviews(filmId, count);
        }
    }
}
