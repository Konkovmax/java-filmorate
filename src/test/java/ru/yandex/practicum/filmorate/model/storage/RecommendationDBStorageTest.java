package ru.yandex.practicum.filmorate.model.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RecommendationDBStorageTest {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final RecommendationDBStorage recommendationStorage;

    @Test
    void recommend() {

        User user1 = new User(1, "User1", "User1Login", "2015-12-28", "User1@mail.ru");
        User user2 = new User(2, "User2", "User2Login", "2015-12-28", "User2@mail.ru");
        User user3 = new User(3, "User3", "User3Login", "2015-12-28", "User3@mail.ru");
        User user4 = new User(4, "User4", "User4Login", "2015-12-28", "User4@mail.ru");
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);
        userStorage.create(user4);
        Film film1 = new Film(1, "Film1", "description1", "2021-10-12", 45, 4, "R");
        Film film2 = new Film(1, "Film2", "description2", "2021-10-12", 45, 4, "R");
        Film film3 = new Film(1, "Film3", "description3", "2021-10-12", 45, 4, "R");
        Film film4 = new Film(1, "Film4", "description4", "2021-10-12", 45, 4, "R");
        Film film5 = new Film(1, "Film5", "description5", "2021-10-12", 45, 4, "R");
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        filmDbStorage.create(film3);
        filmDbStorage.create(film4);
        filmDbStorage.create(film5);
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 3);
        filmDbStorage.addLike(1, 4);
        filmDbStorage.addLike(2, 2);
        filmDbStorage.addLike(3, 1);
        filmDbStorage.addLike(3, 2);
        filmDbStorage.addLike(3, 4);
        filmDbStorage.addLike(4, 1);
        filmDbStorage.addLike(4, 4);
        filmDbStorage.addLike(5, 2);
        filmDbStorage.addLike(5, 3);
        filmDbStorage.addLike(5, 4);
        List<Film> recommends = recommendationStorage.recommendations(2);
        assertEquals(2, recommends.size());
    }
}