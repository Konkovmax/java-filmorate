package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.storages.EventStorage;
import ru.yandex.practicum.filmorate.storages.ImpDAO.EventDbStorage;


import java.util.List;

@Slf4j
@Service
public class EventService {

private final EventStorage eventStorage;

    @Autowired
    EventService(EventDbStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public List<Event> getById(int userId) {
        return eventStorage.getById(userId);
    }
}
