package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
        throwIfNotValid(user);

        if (getUserIdFromDb(user.getLogin()) == 0) {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
                log.warn("Name is empty. Login is used as a name.");
            }
            String createQuery = "insert into users(Name, Login, Birthday, Email) " +
                    "values (?, ?, ?, ?)";
            jdbcTemplate.update(createQuery,
                    user.getName(),
                    user.getLogin(),
                    user.getBirthday(),
                    user.getEmail());
            log.info("User added");
            user.setId(getUserIdFromDb(user.getLogin()));
        }
        return user;
    }

    private boolean throwIfNotValid(User user) throws BadRequestException {
        if (user.getLogin().isEmpty()) {
            log.error("login can't be empty");
            throw new BadRequestException("login can't be empty");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Birthday can't be in the Future");
            throw new BadRequestException("Birthday can't be in the Future");
        }
        if (!user.getEmail().contains("@")) {
            log.error("email should contain @");
            throw new BadRequestException("email should contain @");
        }
        if (user.getLogin().contains(" ")) {
            log.error("login can't contain spaces");
            throw new BadRequestException("login can't contain spaces");
        }
        return true;

    }

    public User update(User user) {
        String createQuery = "update users set Name = ?, Login = ?, Birthday = ?, Email = ? where UserID = ?";
        int updateSuccess = jdbcTemplate.update(createQuery,
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail(),
                user.getId());

        if (updateSuccess == 1) {
            log.info("User updated");
        } else {
            log.warn("user not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    user.getId()));
        }
        return user;
    }

    public User getUser(int userId) {

        try {
            String createQuery = "select * from USERS where userid = ?";
            return jdbcTemplate.queryForObject(createQuery, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
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

    private int getUserIdFromDb(String login) {
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
                "from friends as f1 " +
                "join friends as f2 on f2.userId = ? " +
                "and f2.friendId = f1.friendId " +
                "join users as u on f1.friendId = u.userId " +
                "where f1.userId = ?";

        return jdbcTemplate.query(createQuery, this::mapRowToUser, userId, friendId);
    }

    public void addFriend(int userId, int friendId) {

        if (userExistCheck(userId) == 0) {
            log.warn("User not found");
            throw new NotFoundException(String.format(
                    "User with id: %s not found",
                    userId));
        } else if (userExistCheck(friendId) == 0) {
            log.warn("Friend not found");
            throw new NotFoundException(String.format(
                    "Friend with id: %s not found",
                    friendId));
        } else {
            String createQuery = "insert into friends(UserId, FriendId, status) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(createQuery,
                    userId, friendId, false);
            log.info("Friend added");
        }
    }

    public List<User> getFriends(int userId) {
        try {
            String createQuery = "select u.* " +
                    "from friends as f1 " +
                    "join users as u on f1.friendId = u.userId " +
                    "where f1.userId = ?";
            return jdbcTemplate.query(createQuery, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Friends not found");
            throw new NotFoundException(String.format(
                    "Friends of User with id: %s not found", userId));
        }
    }

    public void removeFriend(int userId, int friendId) {
        String createQuery = "DELETE FROM friends WHERE userid = ? and friendid = ?";
        if (jdbcTemplate.update(createQuery, userId, friendId) == 0) {
            log.warn("Friends not found");
            throw new NotFoundException(String.format(
                    "Friends of User with id: %s not found", userId));
        }
    }
}
