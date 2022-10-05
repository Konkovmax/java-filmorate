package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Optional<Film> update(Film film);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> search(String query, List<String> searchParam);
}

