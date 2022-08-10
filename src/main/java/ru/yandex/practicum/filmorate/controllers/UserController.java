package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class UserController {
    private List<User> users = new ArrayList<>();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    @GetMapping("/users")
    public List<User> findAll() {
        return users;
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) throws ValidationException {
        if (user.getLogin().isEmpty()) {
            log.error("login can't be empty");
            throw new ValidationException("login can't be empty");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Birthday can't be in the Future");
            throw new ValidationException("Birthday can't be in the Future");
        } else if (!user.getEmail().contains("@")) {
            log.error("email should contain @");
            throw new ValidationException("email should contain @");
        } else if (user.getLogin().contains(" ")) {
            log.error("login can't contain spaces");
            throw new ValidationException("login can't contain spaces");
        } else if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
            user.setId(atomicInteger.incrementAndGet());
            users.add(user);
            log.warn("Name is empty. Login is used as a name.");
        } else {
            user.setId(atomicInteger.incrementAndGet());
            users.add(user);
            log.info("User added");
        }
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        //удобнее это было бы сделать через MAP, но в  ТЗ и в постмэне везде про список, поэтому сделал
        //так, можно было бы для GET всё в List переложить, но не понятно что лучше
        for (User currentUser : users) {
            if (currentUser.getId() == user.getId()) {
                users.remove(currentUser);
                users.add(user);
                log.info("User updated");
            } else {
                log.warn("user not found");
                throw new NotFoundException("user not found");
            }
        }
        return user;
    }
}

