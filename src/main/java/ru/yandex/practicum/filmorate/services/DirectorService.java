package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.storages.ImpDAO.DirectorDbStorage;

@Slf4j
@Service
public class DirectorService extends BasicService<Director> {
    @Autowired
    public DirectorService(DirectorDbStorage directorStorage) {
        super(directorStorage, Director.class);
    }
}
