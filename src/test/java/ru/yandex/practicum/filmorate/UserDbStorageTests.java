package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/testschema.sql")
@Sql("/testdata.sql")
class UserDbStorageTests {
	private final UserDbStorage userStorage;// = new UserDbStorage(new JdbcTemplate());

//	@Test
//	void contextLoads() {
//	}

	@Test
	public void testFindUserById() {
		Optional<User> userOptional = Optional.of(userStorage.getUser(1));
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testFindAllUsers() {
		List<User> allUsers = userStorage.findAll();
		assertEquals(3, allUsers.size());
	}

	@Test
	public void testCreateUser() {
		int userId = 4;

		User user = new User(userId, "Name", "login", "1989-02-01", "email@email.ru");
		userStorage.create(user);
		User savedUser = userStorage.getUser(userId);
		savedUser.setId(userId);
		assertEquals(user, savedUser, "Users not equal");
	}
@Test
	public void testUpdateUser() {
		int userId = 3;

		User user = new User(userId, "Name", "login", "1989-02-01", "email@email.ru");
		userStorage.update(user);
		User savedUser = userStorage.getUser(userId);
		savedUser.setId(userId);
		assertEquals(user, savedUser, "Users not equal");
	}
}

