package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    public Map<Integer, User> getUsers() {
        return users;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        throwIfNotValid(user);
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
            user.setId(id);
            generateId();
            users.put(user.getId(), user);
            log.warn("Name is empty. Login is used as a name.");
            return user;
        }
        user.setId(id);
        generateId();
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

    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("User updated");
        } else {
            log.warn("user not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    user.getId()));
        }
        return user;
    }

    public User getUser(int userId) {
        if (!users.containsKey(userId)) {
            log.warn("user not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found", userId));
        }
        log.info("User found");
        return users.get(userId);
    }

    public void generateId() {
        id++;
    }
}
