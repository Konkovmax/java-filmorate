package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private final GenreDbStorage genreStorage;
    private final DirectorDbStorage directorStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreStorage, DirectorDbStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    public Film create(Film film) {
        String sqlQuery = "insert into films(Name, DESCRIPTION, DURATION, RELEASEDATE, MPAID) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILMID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        int filmId = keyHolder.getKey().intValue();
        String createQuery = "insert into FILMS_GENRES (genreid, filmid) " +
                "                values (?, ?)";
        film.setId(filmId);
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(createQuery, genre.getId(), filmId);
        }
        directorStorage.updateDirectorsFromFilm(film);


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
            directorStorage.updateDirectorsFromFilm(film);
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
        String createQuery = "select f.*, R.MPA as mpaName " +
                "from FILMS f " +
                "join MPA R on R.MPAID = F.MPAID where f.FILMID = ?";
        try {
            film = jdbcTemplate.query(createQuery, this::mapRowToFilm, filmId).get(0);
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
        film.setDirectors(directorStorage.getDirectorsFromFilm(film));
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

    public List<Film> search(String query, List<String> searchParam) {
        Set<Film> findBYFilmName = new HashSet<>();
        Set<Film> findBYDirectorName = new HashSet<>();
        query = "%" + query.toLowerCase() + "%";

        if (searchParam.contains("title")) {
            String sqlQuery = "select f.*, r.MPA as mpaName" +
                    "                 from films f" +
                    "                 join MPA R on R.MPAID = F.MPAID" +
                    "                 where lower(f.name) like ?";
            findBYFilmName = Set.copyOf(jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query));
        }
        if (searchParam.contains("director")) {
            String sqlQuery = "select f.*, r.MPA as mpaName" +
                    "                 from films f" +
                    "                 join MPA R on R.MPAID = F.MPAID" +
                    "                 join DIRECTOR D on D.DIRECTORID = F.DIRECTORID" +
                    "                 where D.name like ?";
            findBYDirectorName = Set.copyOf(jdbcTemplate.query(sqlQuery, this::mapRowToFilm,  query));
        }
        if (!findBYDirectorName.isEmpty()) {
            findBYFilmName.addAll(findBYDirectorName);
        }
        return List.copyOf(findBYFilmName);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        String sqlQuery = "select f.*, r.MPA as mpaName" +
                "                 from films f" +
                "                 join MPA R on R.MPAID = F.MPAID" +
                "                 join LIKES L on L.FILMID = F.FILMID" +
                "                 where L.USERSID = ? AND F.FILMID in (" +
                "                    SELECT ff.FILMID from films ff" +
                "                    join LIKES fl on fl.FILMID = FF.FILMID" +
                "                    WHERE FL.USERSID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
    }

}
