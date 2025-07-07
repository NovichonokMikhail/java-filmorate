package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserDbStorageTest {
    private final UserDbStorage userStorage;

    User getTestUser() {
        User user = new User();
        user.setLogin("test_login");
        user.setEmail("testing.creation@gmail.com");
        user.setName("Test Testing");
        user.setBirthday(LocalDate.now());
        return user;
    }

    List<User> getNTestUsers(final int n) {
        return IntStream.range(0, n)
                .boxed()
                .map(i -> {
                    User user = new User();
                    user.setLogin("test_" + i);
                    user.setEmail("test" + i + "@gmail.com");
                    user.setName("Test Testing");
                    user.setBirthday(LocalDate.now().minusMonths(i));
                    return user;
                })
                .toList();
    }

    @Test
    void canCreateUser() {
        User user = getTestUser();
        User createdUser = userStorage.add(user);
        System.out.println(createdUser.getId());
        assert createdUser.getId() == 1;
    }

    @Test
    void canGetUserById() {
        User user = userStorage.add(getTestUser());
        assert userStorage.get(1L).equals(user);
    }

    @Test
    void canGetAllUsers() {
        for (User nTestUser : getNTestUsers(5))
            userStorage.add(nTestUser);
        List<User> users = new ArrayList<>(userStorage.getAll());
        System.out.println(users.size());
        assert !userStorage.getAll().isEmpty();
        assert users.size() == 5;
    }

    @Test
    void canDeleteUser() {
        User user = userStorage.add(getTestUser());
        assert userStorage.getAll().size() == 1;
        assert userStorage.remove(user.getId());
        assert userStorage.getAll().isEmpty();
    }

    @Test
    void canFindCommonFriends() {
        getNTestUsers(3).forEach(userStorage::add);
        userStorage.addFriend(1L, 3L);
        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(2L, 3L);
        assert userStorage.findCommonFriends(1L, 2L).size() == 1;
    }
}
