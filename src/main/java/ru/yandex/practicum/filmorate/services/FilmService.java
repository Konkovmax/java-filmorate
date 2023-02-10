package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;

import ru.yandex.practicum.filmorate.storages.EventStorage;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.ImpDAO.EventDbStorage;
import ru.yandex.practicum.filmorate.storages.ImpDAO.FilmDbStorage;
import ru.yandex.practicum.filmorate.storages.ImpDAO.UserDbStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service

public class FilmService extends BasicService<Film> {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, EventDbStorage eventStorage) {
        super(filmStorage, Film.class);
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    @Override
    public Film update(Film film) {
        List<Genre> genres = film.getGenres();
        if (genres != null && genres.size() > 0) {
            film.setGenres(genres.stream()
                    .distinct()
                    .collect(Collectors.toList()));
        }
        return super.update(film);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
        Event likeEvent = new Event(userId, "LIKE", "ADD");
        likeEvent.setEntityId(filmId);
        eventStorage.create(likeEvent);
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
            eventStorage.create(likeEvent);
        }
    }

    public List<Film> getPopular(Integer year, Integer genreId, int count) {
        if (year == null && genreId == null) {
            return filmStorage.getPopular(count);
        } else if (year == null) {
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

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return getSortedFilms(filmStorage.getCommonFilms(userId, friendId));
    }

    private List<Film> getSortedFilms(List<Film> films) {
        return films.stream().sorted((film0, film1) -> {
            Integer likeFilm0Size = film0.getLikes().size();
            Integer likeFilm1Size = film1.getLikes().size();
            int comp = likeFilm1Size.compareTo(likeFilm0Size);
            return comp;
        }).collect(Collectors.toList());

    }

    public List<Film> getFilmsDirectorSortedByLike(int directorId) {
        return filmStorage.getFilmsDirectorSortedByLike(directorId);
    }

    public List<Film> getFilmsDirectorSortedByYears(int directorId) {
        return filmStorage.getFilmsDirectorSortedByYears(directorId);
    }

    public List<Film> search(String query, String params) {
        String[] items = params.split(",");
        List<String> searchParam = Arrays.asList(items);
        return getSortedFilms(filmStorage.search(query, searchParam));
    }
}
