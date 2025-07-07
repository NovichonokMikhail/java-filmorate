package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAllUsers() {
        return userStorage.getAll();
    }

    public User findUserById(Long id) {
        return userStorage.get(id);
    }

    public Collection<User> getFriendsByUserId(Long id) {
        if (!userStorage.exists(id))
            throw new NotFoundException("User does not exist");
        return findUserById(id)
                .getFriendsList()
                .stream()
                .map(this::findUserById)
                .toList();
    }

    public User createUser(User user) {
        if (user.getName() == null)
            user.setName(user.getLogin());
        log.info("пользователь успешно создан");
        return userStorage.add(user);
    }

    public User updateUser(UserDto userDto) {
        if (userStorage.exists(userDto.getId()))
            return userStorage.modify(userDto);
        throw new NotFoundException("User not found");
    }

    public User addFriend(long userId, long friendId) {
        log.info("добавление друга");
        if (userStorage.exists(userId) && userStorage.exists(friendId))
            return userStorage.addFriend(userId, friendId);
        throw new NotFoundException("One of the users was not found");
    }

    public User deleteFriend(long userId, long friendId) {
        log.info("удаление друга");
        if (userStorage.exists(userId) && userStorage.exists(friendId))
            return userStorage.removeFriend(userId, friendId);
        throw new NotFoundException("One of the users was not found");
    }

    public Collection<User> findCommonFriends(long userId, long otherId) {
        log.info("Поиск общих друзей");
        if (userStorage.exists(userId) && userStorage.exists(otherId))
            return userStorage.findCommonFriends(userId, otherId);
        throw new NotFoundException("One of the users was not found");
    }
}