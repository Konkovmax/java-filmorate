package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.services.EventService;

import java.util.List;

@Slf4j
@RestController
public class EventController {

    private final EventService service;

    @Autowired
    EventController(EventService service) {
        this.service = service;
    }

    @GetMapping(value = "/users/{id}/feed")
    public List<Event> getById (@PathVariable("id") int userId) {
        return service.getById(userId);
    }
}
