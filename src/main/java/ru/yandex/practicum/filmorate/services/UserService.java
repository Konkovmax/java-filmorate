package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.EventDbStorage;
import ru.yandex.practicum.filmorate.storages.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userStorage;
    private final EventDbStorage eventStorage;

    @Autowired
    public UserService(UserDbStorage userStorage, EventDbStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        throwIfNotValid(user);

        if (userStorage.getUserIdFromDb(user.getLogin()) == 0) {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
                log.warn("Name is empty. Login is used as a name.");
            }
            log.info("User added");
            User addedUser = userStorage.create(user);
            addedUser.setId(userStorage.getUserIdFromDb(user.getLogin()));
            return addedUser;
        } else {
            return user;
        }
    }

    public User update(User user) {
        if (userStorage.update(user).isPresent()) {
            return userStorage.update(user).get();
        } else {
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    user.getId()));
        }
    }

    public User getUser(int userId) {
        if (userStorage.getUser(userId).isPresent()) {
            return userStorage.getUser(userId).get();
        } else {
            throw new NotFoundException(String.format(
                    "User with id: %s not found", userId));
        }
    }

    public void delete(int userId) {
        userStorage.delete(userId);

    }

    public void addFriend(int userId, int friendId) {

        if (userStorage.userExistCheck(userId) == 0) {
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    userId));
        } else if (userStorage.userExistCheck(friendId) == 0) {
            log.warn("Friend not found");
            throw new NotFoundException(String.format(
                    "Friend with id: %s not found",
                    friendId));
        } else {
            userStorage.addFriend(userId, friendId);
            Event friendEvent = new Event(userId, "FRIEND", "ADD");
            friendEvent.setEntityId(friendId);
            eventStorage.addEvent(friendEvent);
        }
    }

    public void removeFriend(int userId, int friendId) {
        if (userStorage.userExistCheck(friendId) == 0) {
            log.warn("Friends not found");
            throw new NotFoundException(String.format(
                    "Friends of User with id: %s not found", userId));
        } else {
            userStorage.removeFriend(userId, friendId);
            Event friendEvent = new Event(userId, "FRIEND", "REMOVE");
            friendEvent.setEntityId(friendId);
            eventStorage.addEvent(friendEvent);
        }
    }

    public List<User> getFriends(int userId) {
        if (userStorage.userExistCheck(userId) != 0) {
            return userStorage.getFriends(userId);
        } else {

            log.warn("Friends not found");
            throw new NotFoundException(String.format(
                    "Friends of User with id: %s not found", userId));
        }

    }

    public List<User> getCommonFriends(int userId, int friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }


    private boolean throwIfNotValid(User user) throws BadRequestException {
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
}
