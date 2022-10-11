package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
        String sqlQuery = "INSERT INTO films(name, description, duration, releasedate, mpaid) " +
                "VALUES (?, ?, ?, ?, ?)";
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
        film.setId(filmId);
        String createQuery = "INSERT INTO films_genres (genreid, filmid) " +
                "                VALUES (?, ?)";
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(createQuery, genre.getId(), filmId);
            }
        }
        directorStorage.updateDirectorsFromFilm(film);
        film.setDirectors(directorStorage.getDirectorsFromFilm(film));
        log.info("Film added");
        return film;
    }

    public Optional<Film> update(Film film) {
        String createQuery = "UPDATE films SET name = ?, description = ?, duration = ?, releasedate = ?, mpaid =? WHERE filmid = ?";
        int updateSuccess = jdbcTemplate.update(createQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId());
        if (updateSuccess == 1) {
            createQuery = "DELETE FROM films_genres WHERE  filmid = ? ";
            jdbcTemplate.update(createQuery, film.getId());
            String createQuery2 = "INSERT INTO films_genres(genreid, filmid) VALUES (?, ?)";
            directorStorage.updateDirectorsFromFilm(film);
            film.setDirectors(directorStorage.getDirectorsFromFilm(film));
            if (film.getGenres() != null && film.getGenres().size() > 0) {
                for (Genre genre : film.getGenres()) {
                    try {
                        jdbcTemplate.update(createQuery2, genre.getId(), film.getId());
                    } catch (DataAccessException e) {
                        log.warn("Genres update error");
                    }
                }
            }
            return Optional.of(film);
        } else {
            return Optional.ofNullable(null);
        }
    }

    public List<Film> findAll() {
        String createQuery = "SELECT f.*, r.mpa AS mpaname" +
                "                 FROM films f" +
                "                 JOIN mpa r ON r.mpaid = f.mpaid";
        return jdbcTemplate.query(createQuery, this::mapRowToFilm);
    }

    public Optional<Film> getFilm(int filmId) {
        String createQuery = "SELECT f.*, r.mpa AS mpaname " +
                "FROM films f " +
                "JOIN mpa r ON r.mpaid = f.mpaid WHERE f.filmid = ?";
        final List<Film> films = jdbcTemplate.query(createQuery, this::mapRowToFilm, filmId);
        if (films.size() != 1) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        return Optional.of(films.get(0));
    }

    public boolean delete(int filmId) {
        String createQuery = "DELETE FROM films WHERE filmid = ?";
        var filmToDelete = this.getFilm(filmId);
        if (filmToDelete.isPresent()) {
            jdbcTemplate.update(createQuery, filmId);
            return true;
        } else {
            log.warn("user not found");
            return false;
        }
    }

    public void addLike(int filmId, int userId) {
        String createQuery = "INSERT INTO likes(filmid, usersid) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(createQuery, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {

        String createQuery = "DELETE FROM likes WHERE usersid = ? AND filmid = ?";
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

    public List<Film> getFilmsDirectorSortedByLike(int directorId) {
        //проверили, существует ли такой режжисер
        if (!directorStorage.getDirector(directorId).isEmpty()) {
            String sql = "SELECT f.*,r.mpa AS mpaname FROM films AS f  JOIN films_directors AS fd ON f.filmid = fd.filmid" +
                    " LEFT JOIN  likes l ON f.filmid = l.filmid LEFT JOIN mpa r ON f.mpaid = r.mpaid WHERE directorid=?" +
                    " GROUP BY f.filmid ORDER BY COUNT(usersid) DESC";

            return new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToFilm, directorId));
        } else {
            throw new NotFoundException(String.format(
                    "Director with id: %s not found",
                    directorId));
        }
    }

    public List<Film> getFilmsDirectorSortedByYears(int directorId) {
        //проверили, существует ли такой режжисер
        if (!directorStorage.getDirector(directorId).isEmpty()) {
            String sql = "SELECT f.*,r.mpa AS mpaname FROM films AS f  JOIN films_directors AS fd ON f.filmid = fd.filmid" +
                    " LEFT JOIN mpa r ON f.mpaid = r.mpaid WHERE directorid=? " +
                    "ORDER BY EXTRACT(YEAR FROM CAST(releasedate AS date) )";
            return new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToFilm, directorId));
        } else {
            throw new NotFoundException(String.format(
                    "Director with id: %s not found",
                    directorId));
        }
    }

    private int getFilmIdFromDb(String name) {
        String createQuery = "SELECT f.*, r.mpa AS mpaname" +
                " FROM films f" +
                " JOIN mpa r ON r.mpaid = f.mpaid WHERE f.name = ?";
        try {
            return jdbcTemplate.queryForObject(createQuery, this::mapRowToFilm, name).getId();
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public List<Film> search(String query, List<String> searchParam) {
        List<Film> searchResultList = new ArrayList<>();
        query = "%" + query.toLowerCase() + "%";
        if (searchParam.contains("director")) {
            searchResultList = searchByDirector(query);
        }
        if (searchParam.contains("title")) {
            if (searchResultList.isEmpty()) {
                searchResultList = searchByTitle(query);
            } else {
                searchResultList.addAll(searchByTitle(query));
            }
        }
        return searchResultList;
    }

    private List<Film> searchByTitle(String query) {
        String sqlQuery = "(SELECT f.*, r.mpa AS mpaname" +
                "                 FROM films f" +
                "                 JOIN mpa r ON r.mpaid = f.mpaid" +
                "                 WHERE LOWER(f.name) LIKE ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);
    }

    private List<Film> searchByDirector(String query) {
        String sqlQuery = "(SELECT f.*, r.mpa AS mpaname" +
                "                 FROM films f" +
                "                 JOIN mpa r ON r.mpaid = f.mpaid" +
                "                 JOIN films_directors fd ON fd.filmid = f.filmid" +
                "                 JOIN director d ON d.directorid = fd.directorid" +
                "                 WHERE LOWER(d.name) LIKE ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        String sqlQuery = "SELECT f.*, r.mpa AS mpaname" +
                "                 FROM films f" +
                "                 JOIN mpa r ON r.mpaid = f.mpaid" +
                "                 JOIN likes l ON l.filmid = f.filmid" +
                "                 WHERE l.usersid = ? AND f.filmid IN (" +
                "                    SELECT ff.filmid FROM films ff" +
                "                    JOIN likes fl ON fl.filmid = ff.filmid" +
                "                    WHERE fl.usersid = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
    }

    @Override
    public List<Film> getPopularByGenreAndYear(Integer year, int genreId, int count) {
        String createQuery = "SELECT f.*, r.mpa AS mpaname, COUNT(l.usersid) " +
                "FROM films AS f " +
                " LEFT OUTER JOIN likes AS l " +
                "ON f.filmid = l.filmid " +
                "JOIN mpa r ON r.mpaid = f.mpaid " +
                "JOIN films_genres fg ON f.filmid = fg.filmid " +
                "WHERE YEAR(f.releasedate) = ? AND fg.genreid = ?" +
                "GROUP BY f.filmid " +
                "ORDER BY COUNT(l.usersid) DESC " +
                "LIMIT ?";

        List<Film> film = jdbcTemplate.query(createQuery, this::mapRowToFilm, year, genreId, count);
        log.info("Popular Film By Genre And Year has found");
        return film;
    }

    @Override
    public List<Film> getPopularByGenre(int genreId, int count) {
        String createQuery = "SELECT f.*, r.mpa AS mpaname " +
                "FROM films AS f " +
                " LEFT OUTER JOIN likes AS l " +
                "ON f.filmid = l.filmid " +
                "JOIN mpa r ON r.mpaid = f.mpaid " +
                "JOIN films_genres fg ON f.filmid = fg.filmid " +
                "WHERE fg.genreid = ? " +
                "GROUP BY f.filmid " +
                "ORDER BY COUNT(l.usersid) DESC " +
                "LIMIT ?";

        List<Film> film = jdbcTemplate.query(createQuery, this::mapRowToFilm, genreId, count);
        log.info("Popular Film By Genre has found");
        return film;
    }

    @Override
    public List<Film> getPopularByYear(Integer year, int count) {
        String createQuery = "SELECT f.*, r.mpa AS mpaname " +
                "FROM films AS f " +
                " LEFT OUTER JOIN likes AS l " +
                "ON f.filmid = l.filmid " +
                "JOIN mpa r ON r.mpaid = f.mpaid " +
                "JOIN films_genres fg ON f.filmid = fg.filmid " +
                "WHERE YEAR(f.releasedate) = ? " +
                "GROUP BY f.filmid " +
                "ORDER BY COUNT(l.usersid) DESC " +
                "LIMIT ?";

        List<Film> film = jdbcTemplate.query(createQuery, this::mapRowToFilm, year, count);
        log.info("Popular Film By Year has found");
        return film;
    }

    @Override
    public List<Film> getPopular(int count) {
        String createQuery = "SELECT f.*, r.mpa AS mpaname, COUNT(l.usersid) " +
                "FROM films AS f " +
                " LEFT OUTER JOIN likes AS l " +
                "ON f.filmid = l.filmid " +
                "JOIN mpa r ON r.mpaid = f.mpaid " +
                "GROUP BY f.filmid " +
                "ORDER BY COUNT(l.usersid) DESC, f.name " +
                "LIMIT ?";

        return jdbcTemplate.query(createQuery, this::mapRowToFilm, count);
    }
}

