package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage extends BasicStorage<Review>{

    List<Review> getFilmReviews(int filmId, int count);

    void addReviewReaction(int reviewId, int userId, boolean isLike);

    void removeReviewReaction(int reviewId, int userId, boolean isLike);

}
