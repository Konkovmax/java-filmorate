package ru.yandex.practicum.filmorate.storages.ImpDAO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storages.BasicMethods;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Component
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String createQuery = "SELECT directorid AS id,name FROM director";
        return jdbcTemplate.query(createQuery, new BeanPropertyRowMapper<>(Director.class));
    }

    @Override
    public List<Director> getById(int directorId) {
        String sql = "SELECT directorid AS id,name FROM director WHERE directorid=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class), directorId);
    }

    @Override
    public Director create(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO director(name) VALUES (?)";
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

    @Override
    public void update(Director director) {
        String sql = "UPDATE director SET name=? WHERE directorid=? ";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        log.info("Director updated");
    }

    @Override
    public void delete(int directorId) {
        //удаляем директора из таблицы фильм-директор
        String sql = "DELETE FROM films_directors WHERE directorid=?";
        jdbcTemplate.update(sql, directorId);
        //удаляем директора из таблицы директоров
        String sql2 = "DELETE FROM director WHERE directorid=?";
        jdbcTemplate.update(sql2, directorId);
    }

    @Override
    public List<Director> getDirectorsFromFilm(Film film) {
        String sql = "SELECT d.directorid AS id,d.name FROM director AS d JOIN films_directors AS fd ON d.directorid=fd.directorid WHERE fd.filmid=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class), film.getId());
    }

    @Override
    public void updateDirectorsFromFilm(Film film) {
        List<Director> directors = film.getDirectors();
        //удаляем старые данные если они есть
        String sql = "DELETE FROM films_directors WHERE filmid=?";
        jdbcTemplate.update(sql, film.getId());
        if (directors != null) {
            //проверяем есть ли в базе такие директоры
            directors.forEach(director -> getById(director.getId()).stream().findFirst()
                    .orElseThrow(() -> new NotFoundException(String.format(
                            "Director with id: %s not found",
                            director.getId()))));
            //вписываем новых директоров
            String sql2 = "INSERT INTO films_directors(filmid,directorid) VALUES (?,?)";
            directors.forEach(director -> jdbcTemplate.update(sql2, film.getId(), director.getId()));
        }
    }

}
