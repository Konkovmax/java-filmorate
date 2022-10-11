package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

@Slf4j
@Service
public class EventService {
    private final EventDbStorage eventStorage;

    @Autowired
    EventService(EventDbStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public List<Event> getAllEvents(int userId) { return eventStorage.getAllEvents(userId); }
}
