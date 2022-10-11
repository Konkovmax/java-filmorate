package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @DeleteMapping(value = "/films/{id}")
    public void delete(@PathVariable("id") Integer filmId) { filmService.delete(filmId); }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable("id") Integer filmId) {
        return filmService.getFilm(filmId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopular(@RequestParam(value = "genreId", required = false) Integer genreId,
                                 @RequestParam(value = "count", defaultValue = "10", required = false)
                                 int count,
                                 @RequestParam(value = "year", required = false)
                                     Integer year) {
        return filmService.getPopular(year, genreId, count);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/films/search")
    public List<Film> search(@RequestParam(value = "query", required = true) String query,
                             @RequestParam(value = "by", required = true) String by) {
        return filmService.search(query, by);
    }

    @GetMapping("/films/common")
    public List<Film> getCommonFilms(@RequestParam(value = "userId", required = true) long userId,
                             @RequestParam(value = "friendId", required = true) long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable int directorId, @RequestParam String sortBy) {
        if (sortBy.equals("year")) {
            return filmService.getFilmsDirectorSortedByYears(directorId);
        } else if (sortBy.equals("likes")) {
            return filmService.getFilmsDirectorSortedByLike(directorId);
        } else {
            throw new BadRequestException("Bad Request. Please repeat by correct sortBy");
        }
    }

}
