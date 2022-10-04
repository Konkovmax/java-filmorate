package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RecommendationService recommendationService;


    @GetMapping("/users")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("getFriend");
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") Integer userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/users/{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        return userService.getCommonFriends(userId, friendId);
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }


    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        userService.addFriend(userId, friendId);
    }


    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/recommendations")
    public List<Film> recommendations(@PathVariable("id") Integer userId) {
        return recommendationService.recommendations(userId);
    }

}
