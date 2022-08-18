package ru.yandex.practicum.filmorate.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class UserService {
    private final InMemoryUserStorage userStorage;
    public Map<Integer, User> users;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
         users = userStorage.users;
    }

    public void addFriend(int userId, int friendId) {
        if(!users.containsKey(userId)){
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    userId));
        } else if(!users.containsKey(friendId)){
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "Friend with id: %s not found",
                    friendId));
        } else {
            users.get(userId).addFriend(friendId);
            users.get(friendId).addFriend(userId);
                   log.info("Friend added");

        }
    }

    public void removeFriend(int userId, int friendId) {
        if(users.containsKey(userId)){
            users.get(userId).removeFriend(friendId);
        }
    }

    public List<User> getFriends(int userId){
        List<User> friends = new ArrayList<>();
        for(int friendId: users.get(userId).getFriends()){
           friends.add(users.get(friendId));
           log.info("user "+userId+"friend" +friendId);
        }
    return friends;
    }

    public List<User> getCommonFriends(int userId, int friendId){
        List<User> commonFriends = new ArrayList<>();
        for(int userFriendId: users.get(userId).getFriends()) {
            if (users.get(friendId).getFriends().contains(userFriendId)) {
                commonFriends.add(users.get(userFriendId));
            }
        }
    return commonFriends;
    }
}
