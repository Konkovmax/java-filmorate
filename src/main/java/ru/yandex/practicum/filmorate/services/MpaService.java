package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Mpa;

import ru.yandex.practicum.filmorate.storages.ImpDAO.MpaDbStorage;
import ru.yandex.practicum.filmorate.storages.MpaStorage;
import java.util.List;

@Service
public class MpaService {

    private final MpaStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaStorage) {
        this.mpaDbStorage = mpaStorage;
    }

    public List<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    public Mpa getById(int mpaId) {
        if (mpaDbStorage.getById(mpaId).isPresent()) {
            return mpaDbStorage.getById(mpaId).get();
        } else {

            throw new NotFoundException(String.format(
                    "Mpa with id: %s not found", mpaId));
        }
    }
}
