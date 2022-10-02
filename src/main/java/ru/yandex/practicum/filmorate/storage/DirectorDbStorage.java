package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.el.stream.Optional;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DirectorDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Director> findDirectors() {
        String createQuery = "select * from DIRECTOR";
        return new ArrayList<>(jdbcTemplate.query(createQuery, new BeanPropertyRowMapper<>(Director.class)));
    }

    public Director getDirector(int directorId) {
        String sql = "SELECT*FROM DIRECTOR WHERE DIRECTORID=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class), directorId).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Director with id: %s not found",
                        directorId)));
    }

    public Director createDirector(Director director) {
        String sql = "INSERT INTO DIRECTOR(NAME) VALUES (?)";
        jdbcTemplate.update(sql, director.getName());
        String sql2 = "SELECT DIRECTORID FROM DIRECTOR ORDER BY DIRECTORID DESC LIMIT 1";
        int directorId = jdbcTemplate.queryForObject(sql2, int.class);
        log.info("Director added");
        return getDirector(directorId);
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
        String sql = "DELETE FROM DIRECTOR WHERE DIRECTORID=?";
        jdbcTemplate.update(sql, directorId);
    }

}
