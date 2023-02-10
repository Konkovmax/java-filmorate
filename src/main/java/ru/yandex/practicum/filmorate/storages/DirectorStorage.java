package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface DirectorStorage extends BasicStorage<Director> {

    List<Director> getDirectorsFromFilm(Film film);

    void updateDirectorsFromFilm(Film film);
}
