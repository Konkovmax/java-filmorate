package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;
import java.util.Optional;

public interface BasicMethods<T> {

    List<T> findAll();

    T create(T t);

    Optional<T> update(T t);

    Optional<T> getById(int t);

    boolean delete(int t);

}
