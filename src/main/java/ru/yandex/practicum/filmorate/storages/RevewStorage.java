package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Review;

import java.util.List;
import java.util.Optional;

public interface RevewStorage {
    Review create(Review review);

    Optional<Review> update(Review review);

    List<Review> findAll();

    Optional<Review> getById(int reviewId);

    List<Review> getFilmReviews(int filmId, int count);

    void addReviewReaction(int reviewId, int userId, boolean isLike);

    void removeReviewReaction(int reviewId, int userId, boolean isLike);

    void delete(int reviewId);
}
