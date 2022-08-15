package ru.yandex.practicum.filmorate.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        if(userStorage.users.containsKey(userId)){
            userStorage.users.get(userId).addFriend(friendId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        if(userStorage.users.containsKey(userId)){
            userStorage.users.get(userId).removeFriend(friendId);
        }
    }
}
