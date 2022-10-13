package ru.yandex.practicum.filmorate.storages.ImpDAO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storages.BasicMethods;
import ru.yandex.practicum.filmorate.storages.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        String createQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(createQuery, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getById(int genreId) {
        try {
            String createQuery = "SELECT * FROM genres WHERE genreid = ?";
            return Optional.of(jdbcTemplate.queryForObject(createQuery, this::mapRowToGenre, genreId));

        } catch (EmptyResultDataAccessException e) {
            log.warn("genre not found");
            return Optional.ofNullable(null);
        }
    }

    @Override
    public List<Genre> getFilmsGenre(int filmId) {
        String createQuery = "SELECT g.* " +
                " FROM genres g" +
                " JOIN films_genres f ON g.genreid = f.genreid WHERE f.filmid = ?";
        List<Genre> genres = jdbcTemplate.query(createQuery, this::mapRowToGenre, filmId);
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
