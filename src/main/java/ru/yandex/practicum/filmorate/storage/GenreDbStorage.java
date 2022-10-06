package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> findGenres() {
        String createQuery = "select * from GENRES";
        return jdbcTemplate.query(createQuery, this::mapRowToGenre);
    }

    public Optional<Genre> getGenre(int genreId) {
        try {
            String createQuery = "select * from GENRES where GENREID = ?";
            return Optional.of(jdbcTemplate.queryForObject(createQuery, this::mapRowToGenre, genreId));

        } catch (EmptyResultDataAccessException e) {
            log.warn("genre not found");
            return Optional.ofNullable(null);
        }
    }

    public List<Genre> getFilmsGenre(int filmId) {
        String createQuery = "select g.* " +
                " from GENRES g" +
                " join FILMS_GENRES f on g.GENREID = f.GENREID where f.FILMID = ?";
        List <Genre> genres  = jdbcTemplate.query(createQuery, this::mapRowToGenre, filmId);
        if (genres.size() < 1) {
        return null;
        }
        return genres;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(Integer.parseInt(resultSet.getString("genreid")),
                resultSet.getString("genre"));
    }


}
