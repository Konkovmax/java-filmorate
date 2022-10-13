package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAll();

    List<Director> getById(int directorId);

    Director create(Director director);

    void update(Director director);

    void delete(int directorId);

    List<Director> getDirectorsFromFilm(Film film);

    void updateDirectorsFromFilm(Film film);
}
