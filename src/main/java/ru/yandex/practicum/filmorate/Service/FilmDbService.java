package ru.yandex.practicum.filmorate.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmDbService {
    private final FilmDbStorage filmStorage;
    private Map<Integer, Film> films;

    @Autowired
    public FilmDbService(FilmDbStorage filmStorage) {
        //films = filmStorage.getFilms();
        this.filmStorage = filmStorage;
    }

    private Comparator<Film> comparator = Comparator.comparingInt(t -> t.getLikes().size());

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilm(int filmId) {
        return filmStorage.getFilm(filmId);
    }

    public List<Genre> findGenres() {
        return filmStorage.findGenres();
    }

    public Genre getGenre(int genreId) {
        return filmStorage.getGenre(genreId);
    }
    public List<Mpa> findMpa() {
        return filmStorage.findMpa();
    }

    public Mpa getMpa(int MpaId) {
        return filmStorage.getMpa(MpaId);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
    filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular( count);
    }
}
