package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorDbStorage directorStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> findAllDirectors() {
        return directorStorage.findAllDirectors();
    }

    public Director getDirector(int directorId) {
        return directorStorage.getDirector(directorId);
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director upDateDirector(Director director) {
        return directorStorage.upDateDirector(director);
    }

    public void deleteDirector(int directorId) {
        directorStorage.deleteDirector(directorId);
    }
}
