package ru.yandex.practicum.filmorate.storage.inMemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

@Component
public class InMemoryUserStorage extends UserStorage {
    private final HashMap<Long, User> userInfo = new HashMap<>();

    @Override
    public User add(User user) {
        user.setId(getNextId());
        return userInfo.put(user.getId(), user);
    }

    @Override
    public boolean remove(Long userId) {
        return userInfo.remove(userId) != null;
    }

    @Override
    public User modify(UserDto userDto) {
        User user = userInfo.get(userDto.getId());
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
        return userInfo.put(user.getId(), user);
    }

    @Override
    public User get(Long id) {
        User user = userInfo.get(id);
        if (user != null)
            return user;
        throw new NotFoundException("Пользователя с id: " + id + ". Не существует");
    }

    @Override
    public Collection<User> getAll() {
        return userInfo.values();
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        User user = get(userId);
        user.getFriendsList().add(friendId);
        return userInfo.put(userId, user);
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        User user = get(userId);
        user.getFriendsList().remove(friendId);
        return userInfo.put(userId, user);
    }

    @Override
    public Collection<User> findCommonFriends(final Long userId, final Long otherUserId) {
        User user = userInfo.get(userId);
        User other = userInfo.get(otherUserId);
        Set<Long> othersFriends = other.getFriendsList();
        return user.getFriendsList()
                .stream()
                .filter(othersFriends::contains)
                .map(this::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public boolean exists(Long userId) {
        return userInfo.get(userId) != null;
    }

    private long getNextId() {
        long maxId = userInfo.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
