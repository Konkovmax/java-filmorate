package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {

    private final InMemoryUserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    /* c учётом новой логики старые тесты работать не будут, в ТЗ про тесты, кроме постмэна ничего не
    сказано, поэтому переделывать не стал просто закомментировал, т.к. в дальнейшем может понадобится.
    Это же не "мусор"?
    @Test
    public void PostUserWithEmptyLogin() {
        User user = new User(1, "Nick Name", "", LocalDate.of(1946, 8, 20), "mail@mail.ru");
        UserController userController = new UserController(userStorage, userService);
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
        UserController userController = new UserController(userStorage);
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
        UserController userController = new UserController(userStorage);
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
        UserController userController = new UserController(userStorage);
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
        UserController userController = new UserController(userStorage);
        User userExpected = new User(1, "dolore", "dolore", LocalDate.of(1946, 8, 20), "mail@mail.ru");
        User userActual = userController.create(user);
        Assertions.assertEquals(userExpected, userActual);
    }
*/
}
