package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {

    @Test
    public void PostUserWithEmptyLogin() {
        User user = new User(1, "Nick Name", "", LocalDate.of(1946, 8, 20), "mail@mail.ru");
        UserController userController = new UserController();
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> {
                    userController.create(user);
                });
        Assertions.assertEquals("login can't be empty", ex.getMessage());
    }

    @Test
    public void PostUserWithLoginWithSpaces() {
        User user = new User(1, "Nick Name", " ", LocalDate.of(1946, 8, 20), "mail@mail.ru");
        UserController userController = new UserController();
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> {
                    userController.create(user);
                });
        Assertions.assertEquals("login can't contain spaces", ex.getMessage());
    }

    @Test
    public void PostUserWithFutureBirthday() {
        User user = new User(1, "Nick Name", "dolore", LocalDate.of(2046, 8, 20), "mail@mail.ru");
        UserController userController = new UserController();
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> {
                    userController.create(user);
                });
        Assertions.assertEquals("Birthday can't be in the Future", ex.getMessage());
    }

    @Test
    public void PostUserWithFailEmail() {
        User user = new User(1, "Nick Name", "dolore", LocalDate.of(1946, 8, 20), "mail.ru");
        UserController userController = new UserController();
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> {
                    userController.create(user);
                });
        Assertions.assertEquals("email should contain @", ex.getMessage());
    }

    @Test
    public void PostUserWithEmptyName() {
        User user = new User(1, "", "dolore", LocalDate.of(1946, 8, 20), "mail@mail.ru");
        UserController userController = new UserController();
        User userExpected = new User(1, "dolore", "dolore", LocalDate.of(1946, 8, 20), "mail@mail.ru");
        User userActual = userController.create(user);
        Assertions.assertEquals(userExpected, userActual);
    }

}
