package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    public Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }


    public Film create(Film film) {
        film.setId(id);
        generateId();
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

    public void generateId() {
        id++;
    }
}
