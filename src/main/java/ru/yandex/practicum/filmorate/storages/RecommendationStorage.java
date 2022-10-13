package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface RecommendationStorage {
    List<Film> getAll(int userId);
}
