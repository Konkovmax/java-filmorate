package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private final GenreDbStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    public Film create(Film film) {
        String createQuery = "insert into films(Name, DESCRIPTION, DURATION, RELEASEDATE, MPAID) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(createQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId());
        createQuery = "insert into FILMS_GENRES(genreid, filmid) " +
                "                values (?, ?)";
        film.setId(getFilmIdFromDb(film.getName()));
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(createQuery, genre.getId(), film.getId());
        }
        log.info("Film added");
        return film;
    }

    public Optional<Film> update(Film film) {
        String createQuery = "update films set Name = ?, DESCRIPTION = ?, DURATION = ?, RELEASEDATE = ?, MPAID =? where FILMID = ?";
        int updateSuccess = jdbcTemplate.update(createQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId());
        if (updateSuccess == 1) {
            createQuery = "delete from FILMS_GENRES where  filmid = ? ";
            jdbcTemplate.update(createQuery, film.getId());
            String createQuery2 = "insert into FILMS_GENRES(genreid, filmid) values (?, ?)";
            for (Genre genre : film.getGenres()) {
                try {
                    jdbcTemplate.update(createQuery2, genre.getId(), film.getId());
                } catch (DataAccessException e) {
                    log.warn("Genres update error");
                }
            }
            return Optional.of(film);
        } else {
            return Optional.ofNullable(null);
        }
    }

    public List<Film> findAll() {
        String createQuery = "select f.*, r.MPA as mpaName" +
                "                 from films f" +
                "                 join MPA R on R.MPAID = F.MPAID";
        return jdbcTemplate.query(createQuery, this::mapRowToFilm);
    }

    public Optional<Film> getFilm(int filmId) {
        Film film;
        String createQuery = "select f.*, r.MPA as mpaName" +
                " from films f" +
                " join MPA R on R.MPAID = F.MPAID where f.FILMID = ?";
        try {
            film = jdbcTemplate.queryForObject(createQuery, this::mapRowToFilm, filmId);
            return Optional.of(film);

        } catch (EmptyResultDataAccessException e) {
            log.warn("film not found");
            return Optional.ofNullable(null);
        }
    }

    public boolean delete(int filmId) {
        String createQuery = "delete from FILMS where filmid = ?";
        var filmToDelete = this.getFilm(filmId);
        if (filmToDelete.isPresent()) {
            jdbcTemplate.update(createQuery, filmId);
            return true;
        }
        else {
            log.warn("user not found");
            return false;
        }
    }

    public List<Film> getPopular(int count) {
        String createQuery = "select f.*, r.MPA as mpaName, count(l.USERSID) " +
                "from FILMS as f " +
                " left outer join LIKES as l " +
                "on f.filmId = l.FILMID " +
                "join MPA R on R.MPAID = f.MPAID " +
                "GROUP BY f.FILMID " +
                "order by count(l.USERSID) desc, f.NAME " +
                "limit ?";

        return jdbcTemplate.query(createQuery, this::mapRowToFilm, count);
    }

    public void addLike(int filmId, int userId) {
        String createQuery = "insert into LIKES(filmid, usersid) " +
                "values (?, ?)";
        jdbcTemplate.update(createQuery, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {

        String createQuery = "DELETE FROM likes WHERE usersid = ? and filmid = ?";
        jdbcTemplate.update(createQuery, userId, filmId);
        log.info("Like removed");
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film(Integer.parseInt(resultSet.getString("filmid")),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("releasedate"),
                Integer.parseInt(resultSet.getString("duration")),
                Integer.parseInt(resultSet.getString("mpaid")),
                resultSet.getString("mpaName"));
        List<Genre> genres = genreStorage.getFilmsGenre(film.getId());
        film.setGenres(genres);
        return film;
    }

    private int getFilmIdFromDb(String name) {
        String createQuery = "select f.*, r.mpa as mpaName" +
                " from films f" +
                " join mpa R on R.mpaid = F.mpaid where f.name = ?";
        try {
            return jdbcTemplate.queryForObject(createQuery, this::mapRowToFilm, name).getId();
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
}
