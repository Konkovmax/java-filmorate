package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreDbStorage genreStorage;

    @Autowired
    public GenreService(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findGenres() {
        return genreStorage.findGenres();
    }

    public Genre getGenre(int genreId) {
        if (genreStorage.getGenre(genreId).isPresent()) {
            return genreStorage.getGenre(genreId).get();
        } else {
            throw new NotFoundException(String.format(
                    "Genre with id: %s not found", genreId));
        }
    }
}
