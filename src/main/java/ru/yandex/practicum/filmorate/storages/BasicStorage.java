package ru.yandex.practicum.filmorate.storages;

import java.util.List;
import java.util.Optional;

public interface BasicStorage<T> {

    List<T> findAll();

    T create(T t);

    Optional<T> update(T t);

    Optional<T> getById(int t);

    void delete(int t);

}
