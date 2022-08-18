package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Service.FilmService;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class FilmController {
    private final InMemoryFilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film){
        filmStorage.create(film);
        return film;
    }

    @PutMapping( value = "/films")
    public Film update(@Valid @RequestBody Film film) {
     filmStorage.update(film);
        return film;
    }
}
