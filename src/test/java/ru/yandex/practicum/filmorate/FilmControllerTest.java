package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

public class FilmControllerTest {
    Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void PostFilmWithNegativeDuration() {
        Film film = new Film(1, "Limitless", "Description", LocalDate.of(1946, 8, 20), -150);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(validator.validate(film));
        Assertions.assertTrue(violations.toString().contains("Min.message"));
    }

    @Test
    public void PostFilmWithEmptyName() {
        Film film = new Film(1, "", "Description", LocalDate.of(1946, 8, 20), 150);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(validator.validate(film));
        Assertions.assertTrue(violations.toString().contains("NotEmpty.message"));
    }

    @Test
    public void PostFilmWithFailReleaseDate() {
        Film film = new Film(1, "Limitless", "Description", LocalDate.of(1846, 8, 20), 150);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(validator.validate(film));
        Assertions.assertTrue(violations.toString().contains("ReleaseDate.invalid"));
    }

    @Test
    public void PostFilmWithLongDescription() {
        Film film = new Film(1, "Limitless", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                LocalDate.of(1946, 8, 20), 150);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(violations);
        Assertions.assertTrue(violations.toString().contains("Size.message"));
    }
}
