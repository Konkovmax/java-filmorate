package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreDbService;

import java.util.List;

@RestController
public class GenreController {
    private final GenreDbService genreService;

    @Autowired
    public GenreController(GenreDbService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genres")
    public List<Genre> findGenres() {
        return genreService.findGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable("id") Integer genreId) {
        return genreService.getGenre(genreId);
    }

}
