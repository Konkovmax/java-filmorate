package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storages.FilmDbStorage;
import ru.yandex.practicum.filmorate.storages.GenreDbStorage;
import ru.yandex.practicum.filmorate.storages.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/testschema.sql")
@Sql("/testdata.sql")
class FilmDbStorageTests {
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    @Test
    public void testGetPopular() {
        List<Film> popularFilms = filmStorage.getPopular(2);
        assertEquals("Bon", popularFilms.get(0).getName());
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.getById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindAllFilms() {
        List<Film> allFilms = filmStorage.findAll();
        assertEquals(3, allFilms.size());
    }

    @Test
    public void testCreateFilm() {
        int filmId = 4;
        Film film = new Film(filmId, "Name", "login", "1989-02-01", 70, 4, "R");
        filmStorage.create(film);
        Film savedFilm = filmStorage.getById(filmId).get();
        savedFilm.setId(filmId);
        assertEquals(film, savedFilm, "Films not equal");
    }

    @Test
    public void testUpdateFilm() {
        int filmId = 3;

        Film film = new Film(filmId, "Name", "login", "1989-02-01", 70, 4, "R");
        filmStorage.update(film);
        Film savedFilm = filmStorage.getById(filmId).get();
        savedFilm.setId(filmId);
        assertEquals(film, savedFilm, "Films not equal");
    }


    @Test
    public void testFindGenres() {
        List<Genre> allGenres = genreStorage.findAll();
        assertEquals(6, allGenres.size());
    }

    @Test
    public void testFindMpa() {
        List<Mpa> allMpa = mpaStorage.findAll();
        assertEquals(5, allMpa.size());
    }

    @Test
    public void testFindGenreById() {
        Optional<Genre> filmOptional = genreStorage.getById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindMpaById() {
        Optional<Mpa> filmOptional = mpaStorage.getById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testAddLike() {
        filmStorage.addLike(3, 2);
        assertEquals("Ivan", filmStorage.getPopular(1).get(0).getName());
    }

    @Test
    public void testRemoveLike() {
        filmStorage.removeLike(2, 1);
        assertEquals("Ivan", filmStorage.getPopular(1).get(0).getName());
    }
}
