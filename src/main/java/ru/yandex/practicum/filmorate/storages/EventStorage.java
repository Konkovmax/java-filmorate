package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Event;

import java.util.List;

public interface EventStorage {
    void addEvent(Event event);

    List<Event> getAll(int userId);
}
