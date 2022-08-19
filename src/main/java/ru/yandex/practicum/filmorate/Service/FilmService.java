package ru.yandex.practicum.filmorate.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    public Map<Integer, Film> films;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        films = filmStorage.films;
    }

    private Comparator<Film> comparator = Comparator.comparingInt(t -> t.getLikes().size());

    public Set<Film> sortedFilms = new TreeSet<>(comparator);

    public void addLike(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            log.warn("Film not found");
            throw new NotFoundException(String.format(
                    "Film with id: %s not found",
                    filmId));
        } else {
            films.get(filmId).addLike(userId);

        }
    }

    public void removeLike(int filmId, int userId) {
        if (films.containsKey(filmId)) {
            films.get(filmId).removeLike(userId);
        }
    }

    public List<Film> getPopular(int count) {
        sortedFilms = films.values().stream().collect(Collectors.toSet());
        List<Film> popFilms = new ArrayList<>();
        int j = 0;
        Iterator<Film> i = sortedFilms.iterator();
        while (i.hasNext() & j < count) {
            j++;
            popFilms.add(i.next());
        }
        return popFilms;
    }
}
