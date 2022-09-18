package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/testschema.sql")
@Sql("/testdata.sql")
class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = Optional.of(userStorage.getUser(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindAllUsers() {
        List<User> allUsers = userStorage.findAll();
        assertEquals(3, allUsers.size());
    }

    @Test
    public void testCreateUser() {
        int userId = 4;

        User user = new User(userId, "Name", "login", "1989-02-01", "email@email.ru");
        userStorage.create(user);
        User savedUser = userStorage.getUser(userId);
        savedUser.setId(userId);
        assertEquals(user, savedUser, "Users not equal");
    }

    @Test
    public void testUpdateUser() {
        int userId = 3;

        User user = new User(userId, "Name", "login", "1989-02-01", "email@email.ru");
        userStorage.update(user);
        User savedUser = userStorage.getUser(userId);
        savedUser.setId(userId);
        assertEquals(user, savedUser, "Users not equal");
    }

    @Test
    public void testUserExistCheck() {
        assertEquals(0, userStorage.userExistCheck(5));
    }

    @Test
    public void testCommonFriends() {
        List<User> commonFriends = userStorage.getCommonFriends(1, 2);
        assertEquals("Ivan", commonFriends.get(0).getName());
    }

    @Test
    public void testGetFriends() {
        List<User> friends = userStorage.getFriends(1);
        assertEquals(2, friends.size());
    }

    @Test
    public void testAddFriend() {
        userStorage.addFriend(3, 1);
        assertEquals("Mario", userStorage.getFriends(3).get(0).getName());
    }

    @Test
    public void testRemoveFriends() {
        userStorage.removeFriend(1, 2);
        assertEquals(1, userStorage.getFriends(1).size());
    }
}
