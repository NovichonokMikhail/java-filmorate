package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    void add(User user);

    void remove(User user);

    void modify(User user);

    User get(Long id);

    Collection<User> getAll();
}
