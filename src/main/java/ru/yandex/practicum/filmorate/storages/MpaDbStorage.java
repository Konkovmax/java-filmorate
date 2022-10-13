package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> findAll() {
        String createQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(createQuery, this::mapRowToMpa);
    }

    public Optional<Mpa> getById(int MpaId) {
        try {
            String createQuery = "SELECT * FROM mpa WHERE mpaid = ?";
            return Optional.of(jdbcTemplate.queryForObject(createQuery, this::mapRowToMpa, MpaId));

        } catch (EmptyResultDataAccessException e) {
            log.warn("Mpa not found");
            return Optional.ofNullable(null);
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(Integer.parseInt(resultSet.getString("mpaid")),
                resultSet.getString("mpa"));
    }
}
