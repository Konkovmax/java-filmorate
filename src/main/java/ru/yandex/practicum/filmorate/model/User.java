package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static ru.yandex.practicum.filmorate.FilmorateApplication.DATE_FORMATTER;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Validated
public class User {
    private int id;
    private String name;
    private String login;
    private LocalDate birthday;
    @Email
    private String email;
    private Set<Integer> friends = new HashSet<>();

    public User(int id, String name, String login, String birthday, String email) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.birthday = LocalDate.parse(birthday, DATE_FORMATTER);
        this.email = email;
    }

    private HashMap<Integer, Boolean> mutualFriends = new HashMap<>();

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }

    public Set<Integer> getFriends() {
        return friends;
    }
}
