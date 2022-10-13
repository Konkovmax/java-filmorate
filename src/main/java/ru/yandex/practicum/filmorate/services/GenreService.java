package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storages.GenreDbStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreDbStorage genreStorage;

    @Autowired
    public GenreService(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre getById(int genreId) {
        if (genreStorage.getById(genreId).isPresent()) {
            return genreStorage.getById(genreId).get();
        } else {
            throw new NotFoundException(String.format(
                    "Genre with id: %s not found", genreId));
        }
    }
}
