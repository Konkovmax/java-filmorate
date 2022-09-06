package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    // private Map<Integer, User> users = new HashMap<>();
    private int id = 1;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    public Map<Integer, User> getUsers() {
//        return users;
//    }

    public List<User> findAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    public User create(User user) {
            throwIfNotValid(user);
        String sql = "select * from USERS where login = ?";
        boolean userExist;
        try {
            jdbcTemplate.queryForObject(sql, this::mapRowToUser, user.getLogin());
            userExist = true;
        } catch (EmptyResultDataAccessException e) {
            userExist = false;
        }
        if (!userExist) {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
//            user.setId(id);
//            generateId();
//            users.put(user.getId(), user);
                log.warn("Name is empty. Login is used as a name.");
                // return user;
            }
            user.setId(id);
            //           generateId();
            String sqlQuery = "insert into users(Name, Login, Birthday, Email) " +
                    "values (?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    user.getName(),
                    user.getLogin(),
                    user.getBirthday(),
                    user.getEmail());
            log.info("User added");
        }
        return user;
    }

    public boolean throwIfNotValid(User user) throws BadRequestException {
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
        String sqlQuery = "update users set Name = ?, Login = ?, Birthday = ?, Email = ? where UserID = ?";
        int updateSuccess = jdbcTemplate.update(sqlQuery,
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
//        if (!users.containsKey(userId)) {
//            log.warn("user not found");
//            throw new NotFoundException(String.format(
//                    "User with id: %s not found", userId));
//        }
        log.info("User found");
        String sql = "select * from USERS where userid = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);

    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(Integer.parseInt(resultSet.getString("userid")),
                resultSet.getString("name"),
                resultSet.getString("login"),
                resultSet.getString("birthday"),
                resultSet.getString("email"));
    }

//    public void generateId() {
//        id++;
//    }
}
