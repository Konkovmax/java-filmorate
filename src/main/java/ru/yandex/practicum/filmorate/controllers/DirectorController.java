package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.services.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/directors")
    public List<Director> findAllDirectors() {
        return directorService.findAll();
    }

    @GetMapping("/directors/{id}")
    public Director getDirector(@PathVariable int id) {
        return directorService.get(id);
    }

    @PostMapping("/directors")
    public Director createDirector(@RequestBody @Valid Director director) {
        return directorService.create(director);
    }

    @PutMapping("/directors")
    public Director upDateDirector(@RequestBody @Valid Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirector(@PathVariable int id) {
        directorService.deleteDirector(id);
    }
}
