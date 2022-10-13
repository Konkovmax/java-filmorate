package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Optional;


public interface UserStorage extends BasicStorage<User>{

void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    int getUserIdFromDb(String login);

    int userExistCheck(int id);

    List<User> getCommonFriends(int userId, int friendId);
}
