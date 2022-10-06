package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final EventDbStorage eventStorage;


    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, EventDbStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if(film.getGenres() != null && film.getGenres().size() > 0){
        film.setGenres(film.getGenres().stream()
                .distinct()
                .collect(Collectors.toList()));
    }
        if (filmStorage.update(film).isPresent()) {
            log.info("Film updated");
            return filmStorage.update(film).get();
        } else {
            log.warn("Film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found",
                    film.getId()));
        }
    }

    public void delete(int filmId) {
        var deletedFilm = filmStorage.delete(filmId);
        if (!deletedFilm) {
            throw new NotFoundException(String.format(
                    "Film with id: %s not found", filmId));
        }
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilm(int filmId) {
        if (filmStorage.getFilm(filmId).isPresent()) {
            return filmStorage.getFilm(filmId).get();
        } else {
            throw new NotFoundException(String.format(
                    "Film with id: %s not found", filmId));
        }
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
        Event likeEvent = new Event(userId, "LIKE", "ADD");
        likeEvent.setEntityId(filmId);
        eventStorage.addEvent(likeEvent);
    }

    public void removeLike(int filmId, int userId) {
        if (userStorage.userExistCheck(userId) == 0) {
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    userId));
        } else {
            filmStorage.removeLike(filmId, userId);
            Event likeEvent = new Event(userId, "LIKE", "REMOVE");
            likeEvent.setEntityId(filmId);
            eventStorage.addEvent(likeEvent);
        }
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    public List<Film> getFilmsDirectorSortedByLike(int directorId) {
        return filmStorage.getFilmsDirectorSortedByLike(directorId);
    }

    public List<Film> getFilmsDirectorSortedByYears(int directorId) {
        return filmStorage.getFilmsDirectorSortedByYears(directorId);
    }
    
    public List<Film> search(String query, String params) {
        String [] items = params.split(",");
        List<String> searchParam = Arrays.asList(items);
        return getSortedFilms(filmStorage.search(query, searchParam));

    }   
}
