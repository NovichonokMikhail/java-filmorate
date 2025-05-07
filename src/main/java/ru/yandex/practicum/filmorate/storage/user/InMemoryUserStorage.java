package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> userInfo = new HashMap<>();

    @Override
    public void add(User user) {
        userInfo.put(user.getId(), user);
    }

    @Override
    public void remove(User user) {
        userInfo.remove(user.getId());
    }

    @Override
    public void modify(User user) {
        userInfo.put(user.getId(), user);
    }

    @Override
    public User get(Long id) {
        return userInfo.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return userInfo.values();
    }
}
