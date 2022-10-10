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
        film.setId(filmId);
        String createQuery = "insert into FILMS_GENRES (genreid, filmid) " +
                "                values (?, ?)";
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
        String createQuery = "select f.*, r.MPA as mpaName" +
                "                 from films f" +
                "                 join MPA R on R.MPAID = F.MPAID";
        return jdbcTemplate.query(createQuery, this::mapRowToFilm);
    }

    public Optional<Film> getFilm(int filmId) {
        String createQuery = "select f.*, R.MPA as mpaName " +
                "from FILMS f " +
                "join MPA R on R.MPAID = F.MPAID where f.FILMID = ?";
        final List<Film> films = jdbcTemplate.query(createQuery, this::mapRowToFilm, filmId);
        if (films.size() != 1) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        return Optional.of(films.get(0));
    }

    public boolean delete(int filmId) {
        String createQuery = "delete from FILMS where filmid = ?";
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

    public List<Film> getFilmsDirectorSortedByLike(int directorId) {
        //проверили, существует ли такой режжисер
        if (!directorStorage.getDirector(directorId).isEmpty()) {
            String sql = "SELECT f.*,r.MPA as mpaName FROM FILMS AS F  JOIN FILMS_DIRECTORS AS FD on F.FILMID = FD.FILMID" +
                    " LEFT JOIN  LIKES L on F.FILMID = L.FILMID left join mpa R on F.MPAID = R.MPAID Where DIRECTORID=?" +
                    " GROUP BY F.FILMID ORDER BY COUNT(USERSID) DESC";

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
            String sql = "SELECT f.*,r.MPA as mpaName FROM FILMS AS F  JOIN FILMS_DIRECTORS AS FD on F.FILMID = FD.FILMID" +
                    " left join mpa R on F.MPAID = R.MPAID Where DIRECTORID=? " +
                    "ORDER BY EXTRACT(YEAR FROM CAST(RELEASEDATE AS DATE) )";
            return new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToFilm, directorId));
        } else {
            throw new NotFoundException(String.format(
                    "Director with id: %s not found",
                    directorId));
        }
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
        String sqlQuery = "(select f.*, r.MPA as mpaName" +
                "                 from films f" +
                "                 join MPA R on R.MPAID = F.MPAID" +
                "                 where lower(f.name) like ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);
    }

    private List<Film> searchByDirector(String query) {
        String sqlQuery = "(select f.*, r.MPA as mpaName" +
                "                 from films f" +
                "                 join MPA R on R.MPAID = F.MPAID" +
                "                 join FILMS_DIRECTORS FD on FD.FILMID = F.FILMID" +
                "                 join DIRECTOR D on D.DIRECTORID = FD.DIRECTORID" +
                "                 where lower(D.name) like ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);
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

    @Override
    public List<Film> getPopularByGenreAndYear(Integer year, int genreId, int count) {
        String createQuery = "select f.*, r.MPA as mpaName, count(l.USERSID) " +
                "from FILMS as f " +
                " left outer join LIKES as l " +
                "on f.filmId = l.FILMID " +
                "join MPA R on R.MPAID = f.MPAID " +
                "JOIN FILMS_GENRES FG on f.FILMID = FG.FILMID " +
                "WHERE YEAR(f.RELEASEDATE) = ? AND FG.GENREID = ?" +
                "GROUP BY f.FILMID " +
                "order by count(l.USERSID) desc " +
                "limit ?";

        List<Film> film = jdbcTemplate.query(createQuery, this::mapRowToFilm, year, genreId, count);
        log.info("Popular Film By Genre And Year has found");
        return film;
    }

    @Override
    public List<Film> getPopularByGenre(int genreId, int count) {
        String createQuery = "select f.*, r.MPA as mpaName " +
                "from FILMS as f " +
                " left outer join LIKES as l " +
                "on f.filmId = l.FILMID " +
                "join MPA R on R.MPAID = f.MPAID " +
                "JOIN FILMS_GENRES FG on f.FILMID = FG.FILMID " +
                "WHERE FG.GENREID = ? " +
                "GROUP BY f.FILMID " +
                "order by count(l.USERSID) desc " +
                "limit ?";

        List<Film> film = jdbcTemplate.query(createQuery, this::mapRowToFilm, genreId, count);
        log.info("Popular Film By Genre has found");
        return film;
    }

    @Override
    public List<Film> getPopularByYear(Integer year, int count) {
        String createQuery = "select f.*, r.MPA as mpaName " +
                "from FILMS as f " +
                " left outer join LIKES as l " +
                "on f.filmId = l.FILMID " +
                "join MPA R on R.MPAID = f.MPAID " +
                "JOIN FILMS_GENRES FG on f.FILMID = FG.FILMID " +
                "WHERE YEAR(f.RELEASEDATE) = ? " +
                "GROUP BY f.FILMID " +
                "order by count(l.USERSID) desc " +
                "limit ?";

        List<Film> film = jdbcTemplate.query(createQuery, this::mapRowToFilm, year, count);
        log.info("Popular Film By Year has found");
        return film;
    }

    @Override
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
}

