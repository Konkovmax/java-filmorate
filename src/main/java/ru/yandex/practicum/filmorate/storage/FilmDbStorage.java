package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    private final UserDbStorage userStorage;

    public Map<Integer, Film> getFilms() {
        return films;
    }

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
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
            sqlQuery = "delete from FILMS_GENRES where  filmid = ? ";
                jdbcTemplate.update(sqlQuery, film.getId());
//        if (!film.getGenres().isEmpty()) {
//            sqlQuery = "update FILMS_GENRES set genreid = ? where FILMID =? ";
                String sqlQuery2 = "insert into FILMS_GENRES(genreid, filmid) values (?, ?)";
            //todo fix it "INSERT ignore INTO FILMS_GENRES (genreid, filmid) VALUES (?,?) ON  DUPLICATE KEY UPDATE genreid = ?";
         //   int genreUpdateSuccess = 0;
        //Set<Genre> genres = new HashSet<>(film.getGenres());
            for (Genre genre : film.getGenres()) {
//                genreUpdateSuccess = jdbcTemplate.update(sqlQuery, genre.getId(), film.getId());
//                if(genreUpdateSuccess==0){
                try {
                    jdbcTemplate.update(sqlQuery2, genre.getId(), film.getId());
                }catch (DataAccessException e){
                    log.warn("Genres update error");
                    film.getGenres().remove(genre);
                }
                }
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

    public List<Film> findAll() {
        String sql = "select f.*, r.RATING as ratingName" +
                "                 from films f" +
                "                 join RATINGS R on R.RATINGID = F.RATING";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
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

    public List<Genre> findGenres() {
        String sql = "select * from GENRES";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    public Genre getGenre(int genreId) {
        try {
            String sql = "select * from GENRES where GENREID = ?";
            return  jdbcTemplate.queryForObject(sql, this::mapRowToGenre, genreId);

        } catch (EmptyResultDataAccessException e) {
            log.warn("genre not found");
            throw new NotFoundException(String.format(
                    "Genre with id: %s not found", genreId));
        }
    }

    public List<Mpa> findMpa() {
        String sql = "select * from RATINGS";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    public Mpa getMpa(int MpaId) {
        try {
            String sql = "select * from RATINGS where RATINGID = ?";
            return  jdbcTemplate.queryForObject(sql, this::mapRowToMpa, MpaId);

        } catch (EmptyResultDataAccessException e) {
            log.warn("Mpa not found");
            throw new NotFoundException(String.format(
                    "Mpa with id: %s not found", MpaId));
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

    public void addLike(int filmId, int userId) {
        String sqlQuery = "insert into LIKES(filmid, usersid) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        if (userStorage.userExistCheck(userId)==0) {
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    userId));
        } else {   String sql = "DELETE FROM likes WHERE usersid = ? and filmid = ?";
            jdbcTemplate.update(sql, userId, filmId);
            log.info("Like removed");
        }
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

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(Integer.parseInt(resultSet.getString("ratingid")),
                resultSet.getString("rating"));
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
