package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final InMemoryUserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage userStorage, UserService userService) {
        this.userService = userService;
            this.userStorage = userStorage;
    }
    @GetMapping("/users")
    public List<User> findAll() {
        return  userStorage.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        userStorage.create(user);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
       userStorage.update(user);
        return user;
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId){
        userService.addFriend(userId,friendId);
    }

}

