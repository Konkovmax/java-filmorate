package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storages.MpaDbStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaStorage) {
        this.mpaDbStorage = mpaStorage;
    }

    public List<Mpa> findMpa() {
        return mpaDbStorage.findMpa();
    }

    public Mpa getMpa(int mpaId) {
        if (mpaDbStorage.getMpa(mpaId).isPresent()) {
            return mpaDbStorage.getMpa(mpaId).get();
        } else {

            throw new NotFoundException(String.format(
                    "Mpa with id: %s not found", mpaId));
        }
    }
}
