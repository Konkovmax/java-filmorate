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

    public List<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    public Mpa get(int mpaId) {
        if (mpaDbStorage.get(mpaId).isPresent()) {
            return mpaDbStorage.get(mpaId).get();
        } else {

            throw new NotFoundException(String.format(
                    "Mpa with id: %s not found", mpaId));
        }
    }
}
