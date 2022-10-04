package ru.yandex.practicum.filmorate.model.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        String createQuery = "select * from users";
        return jdbcTemplate.query(createQuery, this::mapRowToUser);
    }

    public User create(User user) {

        String createQuery = "insert into users(Name, Login, Birthday, Email) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(createQuery,
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail());
        return user;
    }


    public Optional<User> update(User user) {
        String createQuery = "update users set Name = ?, Login = ?, Birthday = ?, Email = ? where UserID = ?";
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

    public Optional<User> getUser(int userId) {

        try {
            String createQuery = "select * from USERS where userid = ?";
            return Optional.of(jdbcTemplate.queryForObject(createQuery, this::mapRowToUser, userId));
        } catch (EmptyResultDataAccessException e) {
            log.warn("user not found");
            return Optional.ofNullable(null);
        }

    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(Integer.parseInt(resultSet.getString("userid")),
                resultSet.getString("name"),
                resultSet.getString("login"),
                resultSet.getString("birthday"),
                resultSet.getString("email"));
    }

    public int getUserIdFromDb(String login) {
        String createQuery = "select * from USERS where login = ?";
        try {
            return jdbcTemplate.queryForObject(createQuery, this::mapRowToUser, login).getId();
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public int userExistCheck(int id) {
        String createQuery = "select * from USERS where userid = ?";
        try {
            return jdbcTemplate.queryForObject(createQuery, this::mapRowToUser, id).getId();
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        String createQuery = "select u.* " +
                "from FRIENDS as f1 " +
                "join friends as f2 on f2.userId = ? " +
                "and f2.friendId = f1.friendId " +
                "join users as u on f1.friendId = u.userId " +
                "where f1.userId = ?";

        return jdbcTemplate.query(createQuery, this::mapRowToUser, userId, friendId);
    }


    public void addFriend(int userId, int friendId) {

        String createQuery = "insert into friends(UserId, FriendId, status) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(createQuery,
                userId, friendId, false);
        log.info("Friend added");

    }

    public List<User> getFriends(int userId) {
        String createQuery = "select u.* " +
                "from friends as f1 " +
                "join users as u on f1.friendId = u.userId " +
                "where f1.userId = ?";
        return jdbcTemplate.query(createQuery, this::mapRowToUser, userId);
    }

    public void removeFriend(int userId, int friendId) {
        String createQuery = "DELETE FROM friends WHERE userid = ? and friendid = ?";
        jdbcTemplate.update(createQuery, userId, friendId);
    }
}
