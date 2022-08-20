package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;

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
    public Film create(@Valid @RequestBody Film film) {
        filmStorage.create(film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        filmStorage.update(film);
        return film;
    }

    @GetMapping("/films/popular")
    public List<Film> getPopular(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable("id") Integer filmId) {
        return filmStorage.getFilm(filmId);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        filmService.removeLike(filmId, userId);
    }

}
