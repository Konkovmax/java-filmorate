package ru.yandex.practicum.filmorate.storages.ImpDAO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.BasicMethods;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String createQuery = "SELECT * FROM users";
        return jdbcTemplate.query(createQuery, this::mapRowToUser);
    }

    @Override
    public User create(User user) {

        String createQuery = "INSERT INTO users(name, login, birthday, email) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(createQuery,
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail());
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        String createQuery = "UPDATE users SET name = ?, login = ?, birthday = ?, email = ? WHERE userid = ?";
        int updateSuccess = jdbcTemplate.update(createQuery,
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail(),
                user.getId());

        if (updateSuccess == 1) {
            log.info("User updated");
            return Optional.of(user);
        } else {
            log.warn("user not found");
            return Optional.ofNullable(null);
        }
    }

    @Override
    public Optional<User> getById(int userId) {

        try {
            String createQuery = "SELECT * FROM users WHERE userid = ?";
            return Optional.of(jdbcTemplate.queryForObject(createQuery, this::mapRowToUser, userId));
        } catch (EmptyResultDataAccessException e) {
            log.warn("user not found");
            return Optional.ofNullable(null);
        }

    }
    @Override
    public void delete(int userId) {
        String createQuery = "DELETE FROM users WHERE userid = ?";
        var userToDelete = this.getById(userId);
        if (userToDelete.isPresent()) {
            jdbcTemplate.update(createQuery, userId);
        } else {
            log.warn("user not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found", userId));
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(Integer.parseInt(resultSet.getString("userid")),
                resultSet.getString("name"),
                resultSet.getString("login"),
                resultSet.getString("birthday"),
                resultSet.getString("email"));
    }

    @Override
    public int getUserIdFromDb(String login) {
        String createQuery = "SELECT * FROM users WHERE login = ?";
        try {
            return jdbcTemplate.queryForObject(createQuery, this::mapRowToUser, login).getId();
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public int userExistCheck(int id) {
        String createQuery = "SELECT * FROM users WHERE userid = ?";
        try {
            return jdbcTemplate.queryForObject(createQuery, this::mapRowToUser, id).getId();
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        String createQuery = "SELECT u.* " +
                "FROM friends AS f1 " +
                "JOIN friends AS f2 ON f2.userid = ? " +
                "AND f2.friendid = f1.friendid " +
                "JOIN users AS u ON f1.friendid = u.userid " +
                "WHERE f1.userid = ?";

        return jdbcTemplate.query(createQuery, this::mapRowToUser, userId, friendId);
    }

    @Override
    public void addFriend(int userId, int friendId) {

        String createQuery = "INSERT INTO friends(userid, friendid, status) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(createQuery, userId, friendId, false);
        log.info("Friend added");

    }
    @Override
    public List<User> getFriends(int userId) {
        String createQuery = "SELECT u.* " +
                "FROM friends AS f1 " +
                "JOIN users AS u ON f1.friendid = u.userid " +
                "WHERE f1.userid = ?";
        return jdbcTemplate.query(createQuery, this::mapRowToUser, userId);
    }
    @Override
    public void removeFriend(int userId, int friendId) {
        String createQuery = "DELETE FROM friends WHERE userid = ? AND friendid = ?";
        jdbcTemplate.update(createQuery, userId, friendId);
    }
}
