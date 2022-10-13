package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.storages.DirectorDbStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorService {
    private final DirectorDbStorage directorStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director getDirector(int directorId) {
        return directorStorage.getDirector(directorId).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Director with id: %s not found",
                        directorId)));
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director upDateDirector(Director director) {
        getDirector(director.getId());
        directorStorage.upDateDirector(director);
        return getDirector(director.getId());
    }

    public void deleteDirector(int directorId) {
        getDirector(directorId);
        directorStorage.deleteDirector(directorId);
    }
}
