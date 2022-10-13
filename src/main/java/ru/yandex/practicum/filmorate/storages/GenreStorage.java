package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> getById(int genreId);

    List<Genre> getFilmsGenre(int filmId);
}
