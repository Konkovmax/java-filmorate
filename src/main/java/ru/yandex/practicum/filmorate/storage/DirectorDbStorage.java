package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;


import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DirectorDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Director> findAllDirectors() {
        String createQuery = "select DIRECTORID AS ID,NAME from DIRECTOR";
        return new ArrayList<>(jdbcTemplate.query(createQuery, new BeanPropertyRowMapper<>(Director.class)));
    }

    public Director getDirector(int directorId) {
        String sql = "SELECT DIRECTORID AS ID,NAME FROM DIRECTOR WHERE DIRECTORID=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class), directorId).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Director with id: %s not found",
                        directorId)));
    }

    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO DIRECTOR(NAME) VALUES (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"DIRECTORID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        int directorId = keyHolder.getKey().intValue();
        director.setId(directorId);
        log.info("Director added");
        return director;
    }

    public Director upDateDirector(Director director) {
        //проверили, существует ли такой режжисер
        getDirector(director.getId());
        String sql = "UPDATE DIRECTOR SET NAME=? WHERE DIRECTORID=? ";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        log.info("Director updated");
        return getDirector(director.getId());
    }

    public void deleteDirector(int directorId) {
        //проверили, существует ли такой режжисер
        getDirector(directorId);
        //удаляем директора из таблицы фильм-директор
        String sql = "DELETE FROM FILMS_DIRECTORS WHERE DIRECTORID=?";
        jdbcTemplate.update(sql, directorId);
        //удаляем директора из таблицы директоров
        String sql2 = "DELETE FROM DIRECTOR WHERE DIRECTORID=?";
        jdbcTemplate.update(sql2, directorId);
    }

    public List<Director> getDirectorsFromFilm(Film film) {
        String sql = "SELECT D.DIRECTORID AS ID,D.NAME FROM DIRECTOR AS D JOIN FILMS_DIRECTORS AS FD ON D.DIRECTORID=FD.DIRECTORID WHERE FD.FILMID=?";
        return new ArrayList<>(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class), film.getId()));
    }

    public void updateDirectorsFromFilm(Film film) {
        List<Director> directors = film.getDirectors();
        //удаляем старые данные если они есть
        String sql = "DELETE FROM FILMS_DIRECTORS WHERE FILMID=?";
        jdbcTemplate.update(sql, film.getId());
        if (directors != null) {
            //проверяем есть ли в базе такие директоры
            directors.forEach(director -> getDirector(director.getId()));
            //вписываем новых директоров
            String sql2 = "INSERT INTO FILMS_DIRECTORS(FILMID,DIRECTORID) VALUES (?,?)";
            directors.forEach(director -> jdbcTemplate.update(sql2, film.getId(), director.getId()));
        }
    }

}
