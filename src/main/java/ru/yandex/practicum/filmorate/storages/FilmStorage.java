package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Optional<Film> update(Film film);

    Optional<Film> getById(int filmId);

    boolean delete(int filmId);

    List<Film> getCommonFilms(long userId, long friendId);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getFilmsDirectorSortedByLike(int directorId);

    List<Film> getFilmsDirectorSortedByYears(int directorId);

    List<Film> search(String query, List<String> searchParam);

    List<Film> getPopularByGenreAndYear(Integer year, int genreId, int count);

    List<Film> getPopularByGenre(int genreId, int count);

    List<Film> getPopularByYear(Integer year, int count);

    List<Film> getPopular(int count);
}

