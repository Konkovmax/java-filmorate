package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<Mpa> findMpa() {
        String createQuery = "select * from RATINGS";
        return jdbcTemplate.query(createQuery, this::mapRowToMpa);
    }

    public Mpa getMpa(int MpaId) {
        try {
            String createQuery = "select * from RATINGS where RATINGID = ?";
            return jdbcTemplate.queryForObject(createQuery, this::mapRowToMpa, MpaId);

        } catch (EmptyResultDataAccessException e) {
            log.warn("Mpa not found");
            throw new NotFoundException(String.format(
                    "Mpa with id: %s not found", MpaId));
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(Integer.parseInt(resultSet.getString("ratingid")),
                resultSet.getString("rating"));
    }
}
