package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@Service
public class GenreDbService {
    private final GenreDbStorage genreStorage;

    @Autowired
    public GenreDbService(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findGenres() {
        return genreStorage.findGenres();
    }

    public Genre getGenre(int genreId) {
        return genreStorage.getGenre(genreId);
    }
}
