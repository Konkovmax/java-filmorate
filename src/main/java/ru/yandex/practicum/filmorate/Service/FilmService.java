package ru.yandex.practicum.filmorate.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private Map<Integer, Film> films;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        films = filmStorage.getFilms();
        this.filmStorage = filmStorage;
    }

    private Comparator<Film> comparator = Comparator.comparingInt(t -> t.getLikes().size());

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilm(int filmId) {
        return filmStorage.getFilm(filmId);
    }

    public void addLike(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            log.warn("Film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found",
                    filmId));
        } else {
            films.get(filmId).addLike(userId);

        }
    }

    public void removeLike(int filmId, int userId) {
        if (!films.containsKey(userId)) {
            log.warn("Film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found",
                    filmId));
        } else if (!films.get(filmId).getLikes().contains(userId)) {
            log.warn("Like not found");
            throw new NotFoundException(String.format(
                    "Like from User with id: %s not found",
                    userId));
        } else {
            log.info("Like removed");
            films.get(filmId).removeLike(userId);
        }
    }

    public List<Film> getPopular(int count) {
        List<Film> popFilms = films.values().stream().sorted(comparator.reversed()).collect(Collectors.toList());
        if (count > popFilms.size()) {
            count = popFilms.size();
        }
        return popFilms.subList(0, count);
    }
}
