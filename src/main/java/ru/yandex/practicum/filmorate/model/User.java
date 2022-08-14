package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Validated
public class User {
    private  int id;
    private  String name;
    private String login;
    private LocalDate birthday;
    @Email
    private String email;


}
