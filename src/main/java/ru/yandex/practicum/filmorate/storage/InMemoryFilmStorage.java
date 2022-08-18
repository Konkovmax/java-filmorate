package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{

    public Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }


    public Film create( Film film){
        film.setId(id);
        generateId();
        films.put(film.getId(), film);
        log.info("Film added");
        return film;
    }


    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film updated");
        } else {
            log.warn("Film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found",
                    film.getId()));
        }
        return film;
    }

    public void generateId(){
        id++;
    }
}
