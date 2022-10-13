package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;
import ru.yandex.practicum.filmorate.storages.ImpDAO.DirectorDbStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director getById (int directorId) {
        return directorStorage.getById(directorId).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Director with id: %s not found",
                        directorId)));
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        getById(director.getId());
        directorStorage.update(director);
        return getById(director.getId());
    }

    public void delete (int directorId) {
        getById(directorId);
        directorStorage.delete(directorId);
    }
}
