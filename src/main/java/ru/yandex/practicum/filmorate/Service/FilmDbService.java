package ru.yandex.practicum.filmorate.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.util.List;

@Slf4j
@Service
public class FilmDbService {
    private final FilmDbStorage filmStorage;

    @Autowired
    public FilmDbService(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

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
        return filmStorage.getPopular(count);
    }
}
