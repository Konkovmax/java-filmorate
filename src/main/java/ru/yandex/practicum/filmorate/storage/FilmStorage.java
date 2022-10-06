package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Optional<Film> update(Film film);

    List<Film> getCommonFilms(long userId, long friendId);
    List<Film> getPopularByGenreAndYear(Integer year, int genreId, int count);
    List<Film> getPopularByGenre(int genreId, int count);
    List<Film> getPopularByYear(Integer year, int count);
    List<Film> getPopular(int count);
}

