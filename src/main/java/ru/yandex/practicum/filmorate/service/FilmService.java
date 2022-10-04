package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.storage.UserDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;


    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;

    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        film.setGenres(film.getGenres().stream()
                .distinct()
                .collect(Collectors.toList()));
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
    }

    public void removeLike(int filmId, int userId) {
        if (userStorage.userExistCheck(userId) == 0) {
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    userId));
        } else {
            filmStorage.removeLike(filmId, userId);
        }
    }

//    public List<Film> getPopular(int count) {
//        return filmStorage.getPopular(count);
//    }

    public List<Film> getPopularByGenreAndYear(Integer year, Integer genreId, Integer count) {
        if(year == null && genreId == null){
            return filmStorage.getPopular(count);
        }
        else if (year == null) {
            //Метод по жанрам
            return filmStorage.getPopularByGenre(genreId, count);
        } else if (genreId == null) {
            //Метод по годам
            return filmStorage.getPopularByYear(year, count);
        } else {
            //Метод по годам и жанрам
            return filmStorage.getPopularByGenreAndYear(year, genreId, count);
        }
    }
}
