package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Repository
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage extends UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper userMapper;
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";

    @Override
    public User add(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String query = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        final Number id = keyHolder.getKey();
        if (id != null)
            return get(id.longValue());
        throw new InternalError("Ошибка сохранения данных");
    }

    @Override
    public boolean remove(Long userId) {
        final String query = "DELETE FROM users WHERE user_id = ?";
        log.debug("Deleting user with id: {}", userId);
        return jdbc.update(query, userId) > 0;
    }

    @Override
    public User modify(UserDto userDto) {
        final long userId = userDto.getId();
        User user = get(userId);
        if (userDto.hasEmail())
            user.setEmail(userDto.getEmail());
        if (userDto.hasName())
            user.setName(userDto.getName());
        if (userDto.hasLogin() && (user.getLogin().equals(user.getName()))) {
            user.setLogin(user.getLogin());
            user.setName(user.getName());
        } else if (userDto.hasLogin())
            user.setLogin(userDto.getLogin());
        if (userDto.hasBirthday())
            user.setBirthday(userDto.getBirthday());

        final String query = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbc.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), userId);
        return get(userId);
    }

    @Override
    public User get(Long id) {
        if (!exists(id))
            throw new NotFoundException("Пользователя с id: " + id + ", не существует");
        final User user = jdbc.queryForObject(SELECT_USER_BY_ID, userMapper, id);
        user.setFriendsList(getFriends(user.getId()));
        return user;
    }

    @Override
    public Collection<User> getAll() {
        List<User> users = jdbc.query(SELECT_ALL_USERS, userMapper);
        for (User user : users)
            user.setFriendsList(getFriends(user.getId()));
        return users;
    }

    @Override
    public User addFriend(final Long userId, final Long friendId) {
        final String query = "INSERT INTO user_friend_request (user_id, friend_id) VALUES (?, ?)";
        jdbc.update(query, userId, friendId);
        return get(userId);
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        final String query = "DELETE FROM user_friend_request WHERE user_id = ? AND friend_id = ?";
        jdbc.update(query, userId, friendId);
        return get(userId);
    }

    @Override
    public Collection<User> findCommonFriends(Long userId, Long otherUserId) {
        final String query = """
                SELECT
                   u.*
                FROM
                   USER_FRIEND_REQUEST ufr1
                INNER JOIN
                   USER_FRIEND_REQUEST ufr2 ON ufr1.friend_id = ufr2.friend_id
                INNER JOIN
                   users AS u ON ufr1.friend_id = u.user_id
                WHERE
                   ufr1.user_id = ?
                   AND ufr2.user_id = ?""";
        return jdbc.query(query, userMapper, userId, otherUserId);
    }

    @Override
    public boolean exists(final Long userId) {
        return !jdbc.query(SELECT_USER_BY_ID, userMapper, userId).isEmpty();
    }

    private Set<Long> getFriends(final Long userId) {
        final String friendsQuery = "SELECT friend_id FROM user_friend_request WHERE user_id = ?";
        return new HashSet<>(jdbc.query(friendsQuery,
                (rs, rowNum) -> rs.getLong("friend_id"), userId));
    }
}
