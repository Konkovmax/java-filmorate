package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Validated
public class User {
    private  int id;
    private  String name;
    private String login;
    private LocalDate birthday;
    @Email
    private String email;
    private Set<Integer> friends;

    public void addFriend(int friendId){
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }

    public Set<Integer> getFriends(){
        return friends;
    }
}
