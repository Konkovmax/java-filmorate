package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    public Map<Integer, Film> getFilms() {
        return films;
    }

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Film> findAll() {
        String sql = "select f.*, r.RATING as ratingName" +
                "                 from films f" +
                "                 join RATINGS R on R.RATINGID = F.RATING";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }


    public Film create(Film film) {
        String sqlQuery = "insert into films(Name, DESCRIPTION, DURATION, RELEASEDATE, RATING) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId());
        sqlQuery = "insert into FILMS_GENRES(genreid, filmid) " +
                "                values (?, ?)";
        film.setId(getFilmIdFromDb(film.getName()));
        for (Genre genre :film.getGenres()){
            jdbcTemplate.update(sqlQuery, genre.getId(), film.getId());
        }
        log.info("Film added");
        return film;
    }

    public Film update(Film film) {
        String sqlQuery = "update films set Name = ?, DESCRIPTION = ?, DURATION = ?, RELEASEDATE = ?, RATING =? where FILMID = ?";
        int updateSuccess = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId());

        if (updateSuccess == 1) {
            log.info("Film updated");
        } else {
            log.warn("Film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found",
                    film.getId()));
        }
        return film;
    }

    public Film getFilm(int filmId) {
        Film film;
        String sql = "select f.*, r.RATING as ratingName" +
                " from films f" +
                " join RATINGS R on R.RATINGID = F.RATING where f.FILMID = ?";
        try {
            film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
            sql = "select g.* " +
                    " from GENRES g" +
                    " join FILMS_GENRES f on g.GENREID = f.GENREID where f.FILMID = ?";
            List<Genre> genres = jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
            film.setGenres(genres);
            return film;

        } catch (EmptyResultDataAccessException e) {
            log.warn("film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found", filmId));
        }
    }

    public List<Film> getPopular(int count) {
        String sql = "select f.*, r.RATING as ratingName, count(l.USERSID) " +
                "from FILMS as f " +
                " left outer join LIKES as l " +
                "on f.filmId = l.FILMID " +
                "join RATINGS R on R.RATINGID = f.RATING " +
                "GROUP BY f.FILMID " +
                "order by count(l.USERSID) desc " +
                "limit ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film(Integer.parseInt(resultSet.getString("filmid")),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("releasedate"),
                Integer.parseInt(resultSet.getString("duration")),
                Integer.parseInt(resultSet.getString("rating")),
                resultSet.getString("ratingName"));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(Integer.parseInt(resultSet.getString("genreid")),
                resultSet.getString("genre"));
    }

    private int getFilmIdFromDb(String name) {
        String sql = "select f.*, r.RATING as ratingName" +
                " from films f" +
                " join RATINGS R on R.RATINGID = F.RATING where f.name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, name).getId();
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
}
