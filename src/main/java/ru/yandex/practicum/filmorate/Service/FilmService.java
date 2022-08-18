package ru.yandex.practicum.filmorate.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Map;
@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    public Map<Integer, Film> films;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        films = filmStorage.films;
    }
}
