package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    @GetMapping("/users")
    public List<User> findAll() {
        return  new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        throwIfNotValid(user);
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
                user.setId(atomicInteger.incrementAndGet());
                users.put(user.getId(), user);
                log.warn("Name is empty. Login is used as a name.");
                return user;
            }
            user.setId(atomicInteger.incrementAndGet());
            users.put(user.getId(), user);
            log.info("User added");

        return user;
    }


    public boolean throwIfNotValid(User user) throws BadRequestException {
        if (user.getLogin().isEmpty()) {
            log.error("login can't be empty");
            throw new BadRequestException("login can't be empty");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Birthday can't be in the Future");
            throw new BadRequestException("Birthday can't be in the Future");
        }
        if (!user.getEmail().contains("@")) {
            log.error("email should contain @");
            throw new BadRequestException("email should contain @");
        }
        if (user.getLogin().contains(" ")) {
            log.error("login can't contain spaces");
            throw new BadRequestException("login can't contain spaces");
        }
        return true;

    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("User updated");
        } else {
            log.warn("user not found");
            throw new NotFoundException("user not found");
        }
        return user;
    }
}

