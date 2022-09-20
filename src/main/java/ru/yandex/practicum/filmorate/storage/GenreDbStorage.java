package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public Genre getGenre(int genreId) {
        try {
            String createQuery = "select * from GENRES where GENREID = ?";
            return jdbcTemplate.queryForObject(createQuery, this::mapRowToGenre, genreId);

        } catch (EmptyResultDataAccessException e) {
            log.warn("genre not found");
            throw new NotFoundException(String.format(
                    "Genre with id: %s not found", genreId));
        }
    }

    public List<Genre> getFilmsGenre(int filmId) {
        String createQuery = "select g.* " +
                " from GENRES g" +
                " join FILMS_GENRES f on g.GENREID = f.GENREID where f.FILMID = ?";
        return jdbcTemplate.query(createQuery, this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(Integer.parseInt(resultSet.getString("genreid")),
                resultSet.getString("genre"));
    }


}
