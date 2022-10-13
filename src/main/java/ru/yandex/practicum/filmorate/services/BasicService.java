package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storages.BasicStorage;

import java.util.List;
import java.util.Optional;

abstract class BasicService<T> {

    protected final BasicStorage storage;
    final Class<T> typeParameterClass;
    BasicService(BasicStorage storage, Class<T> typeParameterClass) {
        this.storage = storage;
        this.typeParameterClass = typeParameterClass;
    }

    public List<T> findAll() {
        return storage.findAll();
    }

    public T getById (int id) {
        if (storage.getById(id).isPresent()) {
            return (T) storage.getById(id).get();
        } else {
            throw new NotFoundException(String.format(
                    "%s with id: %s not found", typeParameterClass.getName(),id));
        }
    }

    public T create(T item) {
        return (T) storage.create(item);
    }

    public T update(T item) {
        Optional<T> updateResult = storage.update(item);
        if (updateResult.isPresent()) {
            return (T) storage.update(item).get();
        } else {
            throw new NotFoundException(String.format(
                    "%s with not found", typeParameterClass.getName()));
        }
    }

    public void delete (int id) {
        getById(id);
        storage.delete(id);
    }

}
