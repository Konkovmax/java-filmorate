package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;
@Service
public class MpaDbService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaDbService(MpaDbStorage mpaStorage) {
        this.mpaDbStorage = mpaStorage;
    }
    public List<Mpa> findMpa() {
        return mpaDbStorage.findMpa();
    }

    public Mpa getMpa(int MpaId) {
        return mpaDbStorage.getMpa(MpaId);
    }
}
