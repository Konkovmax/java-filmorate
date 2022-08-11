package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) throws BadRequestException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("wrong release date");
            throw new BadRequestException("wrong release date");
        }
        film.setId(atomicInteger.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Film added");
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film updated");
        } else {
            log.warn("Film not found");
            throw new NotFoundException("film not found");
        }
        return film;
    }
}




