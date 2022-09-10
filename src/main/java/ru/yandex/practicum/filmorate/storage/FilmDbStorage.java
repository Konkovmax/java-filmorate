package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    public Map<Integer, Film> getFilms() {
        return films;
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }


    public Film create(Film film) {
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Film added");
        return film;
    }


    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
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
        if (!films.containsKey(filmId)) {
            log.warn("film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found", filmId));
        }
        log.info("Film found");
        return films.get(filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film(Integer.parseInt(resultSet.getString("filmid")),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("releasedate"),
                Integer.parseInt(resultSet.getString("duration")),
                Integer.parseInt(resultSet.getString("rating")));
    }
}
