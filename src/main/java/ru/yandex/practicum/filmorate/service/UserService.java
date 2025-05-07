package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserStorage userStorage;

    public Collection<User> findAllUsers() {
        return userStorage.getAll();
    }

    public User findUserById(Long id) {
        User user = userStorage.get(id);
        if (user == null)
            throw new NotFoundException("Пользователь с id " + id + " - не найден.");
        return user;
    }

    public Collection<User> getFriendsByUserId(Long id) {
        return findUserById(id)
                .getFriendsList()
                .stream()
                .map(this::findUserById)
                .toList();
    }

    public User createUser(User user) {
        if (user.getName() == null)
            user.setName(user.getLogin());
        user.setId(getNextId());
        userStorage.add(user);
        log.info("пользователь успешно создан");
        return user;
    }

    public User updateUser(User user) {
        Long userId = user.getId();
        if (userId == null)
            throw new ValidationException("Id не может быть пустым");
        User userToUpdate = userStorage.get(userId);
        if (userToUpdate == null)
            throw new NotFoundException(String.format("Пользователь с id = %d, не существует", userId));
        userToUpdate.setBirthday(user.getBirthday());
        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setLogin(user.getLogin());
        userStorage.modify(userToUpdate);
        log.info("информация о пользователе успешно обновлена");
        return userToUpdate;
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.get(userId);
        // проверка на наличие обоих пользователей
        if (user == null)
            throw new NotFoundException("Пользователя с id: " + userId + ". Не существует");
        User friend = userStorage.get(friendId);
        if (friend == null)
            throw new NotFoundException("Пользователя с id: " + friendId + ". Не существует");
        // проверка на дружбу
        if (user.getFriendsList().contains(friendId))
            throw new ValidationException(
                    String.format("Пользователи c id: %d и %d, уже являются друзьями", userId, friendId)
            );
        user.getFriendsList().add(friendId);
        friend.getFriendsList().add(userId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        log.info("удаление друга");
        User user = userStorage.get(userId);
        if (user == null)
            throw new NotFoundException("Пользователя с id: " + userId + ". Не существует");
        User friend = userStorage.get(friendId);
        if (friend == null)
            throw new NotFoundException("Пользователя с id: " + friendId + ". Не существует");
        if (!user.getFriendsList().contains(friendId))
            return user;
        user.getFriendsList().remove(friendId);
        friend.getFriendsList().remove(userId);
        log.info("друг удален");
        return user;
    }

    public Collection<User> findCommonFriends(long userId, long otherId) {
        log.info("Поиск общих друзей");
        User user = findUserById(userId);
        if (user == null)
            throw new NotFoundException("Пользователя с id: " + userId + ". Не существует");
        User other = userStorage.get(otherId);
        if (other == null)
            throw new NotFoundException("Пользователя с id: " + otherId + ". Не существует");
        Set<Long> othersFriends = other.getFriendsList();
        return user.getFriendsList()
                .stream()
                .filter(othersFriends::contains)
                .map(this::findUserById)
                .toList();
    }

    private long getNextId() {
        long maxId = userStorage.getAll()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++maxId;
    }
}