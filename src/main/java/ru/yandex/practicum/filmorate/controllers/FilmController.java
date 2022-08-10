package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class FilmController {
    //сначала я сделал всё для user ов, потом прочитал доп задание про @validated и мне очень приглянулась
    //эта идея, поэтому film-ы я делал через @valid то есть одно так второе подругому, но ТЗ не противоречит,
    // поэтому оставил так
    private List<Film> films = new ArrayList<>();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    @GetMapping("/films")
    public List<Film> findAll() {
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("wrong release date");
            throw new ValidationException("wrong release date");
        } else {
            film.setId(atomicInteger.incrementAndGet());
            films.add(film);
            log.info("Film added");
        }
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        for (Film currentFilm : films) {
            if (currentFilm.getId() == film.getId()) {
                films.remove(currentFilm);
                films.add(film);
                log.info("Film updated");
            } else {
                log.warn("Film not found");
                throw new NotFoundException("film not found");
            }
        }
        return film;
    }
}




