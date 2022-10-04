package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ReviewDbStorage {

    private final GenreDbStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

   public Review create(Review review) {
        String createQuery = "insert into reviews(Content, isPositive, filmId, userId) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
       jdbcTemplate.update(connection -> {
           PreparedStatement stmt = connection.prepareStatement(createQuery, new String[]{"REVIEWID"});
           stmt.setString(1, review.getContent());
           stmt.setBoolean(2, review.getIsPositive());
           stmt.setInt(3,  review.getFilmId());
           stmt.setInt(4, review.getUserId());
           return stmt;
       }, keyHolder);
       int reviewId = keyHolder.getKey().intValue();
       review.setReviewId(reviewId);
        log.info("Review added");
        return review;
    }

    public Optional<Review> update(Review review) {
        String createQuery = "update reviews set content = ?, ispositive = ?" +
                //", filmid = ?, userid = ? " +
                " where reviewID = ?";
        int updateSuccess = jdbcTemplate.update(createQuery,
                review.getContent(),
                review.getIsPositive(),
//                review.getFilmId(),
//                review.getUserId(),
                review.getReviewId());
        if (updateSuccess == 1) {
            return Optional.of(review);
        } else {
            return Optional.ofNullable(null);
        }
    }

    public List<Review> findAllReviews() {
        String createQuery = "select * " +
                "                 from reviews";
        return jdbcTemplate.query(createQuery, this::mapRowToReview);
    }

    public Optional<Review> getReview(int reviewId) {
        Review review;
        String createQuery = "select * " +
                " from reviews" +
                " where reviewid = ?";
        try {
            review = jdbcTemplate.queryForObject(createQuery, this::mapRowToReview, reviewId);
            return Optional.of(review);

        } catch (EmptyResultDataAccessException e) {
            log.warn("film not found");
            return Optional.ofNullable(null);
        }
    }

    public List<Review> getFilmReviews(int filmId, int count) {
        String createQuery = "select * " +
                "from reviews " +
                " where filmid = ? " +
                "limit ?";

        return jdbcTemplate.query(createQuery, this::mapRowToReview, filmId, count);
    }

    public void addReviewReaction(int reviewId, int userId, boolean isLike) {
        String createQuery = "insert into review_scores(reviewid, userid, islike) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(createQuery, reviewId, userId, isLike);
    }

    public void removeLike(int filmId, int userId) {

        String createQuery = "DELETE FROM likes WHERE usersid = ? and filmid = ?";
        jdbcTemplate.update(createQuery, userId, filmId);
        log.info("Like removed");
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review(Integer.parseInt(resultSet.getString("reviewid")),
                resultSet.getString("content"),
                resultSet.getBoolean("isPositive"),
                Integer.parseInt(resultSet.getString("userid")),
                Integer.parseInt(resultSet.getString("filmid")));
        review.setUseful(getReviewUseful(review.getReviewId()));
        return review;
    }

    private int getReviewUseful(int reviewId){
        int useful = 0;
        String createQuery = "select count(reviewId) " +
        "from review_scores " +
                "where reviewid = ? and islike = ?";
        useful = jdbcTemplate.queryForObject(createQuery, Integer.class, reviewId, true);
        useful -= jdbcTemplate.queryForObject(createQuery, Integer.class, reviewId, false);


//                "GROUP BY f.FILMID " +
//                "order by count(l.USERSID) desc, f.NAME " +


    return useful;
    }

//    private int getFilmIdFromDb(String name) {
//        String createQuery = "select f.*, r.mpa as mpaName" +
//                " from films f" +
//                " join mpa R on R.mpaid = F.mpaid where f.name = ?";
//        try {
//            return jdbcTemplate.queryForObject(createQuery, this::mapRowToFilm, name).getId();
//        } catch (EmptyResultDataAccessException e) {
//            return 0;
//        }
//    }
}
