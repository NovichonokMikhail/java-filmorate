package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Long, User> usersInfo = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("получаем всех пользователей");
        return usersInfo.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null)
            user.setName(user.getLogin());
        user.setId(getNextId());
        usersInfo.put(user.getId(), user);
        log.info("пользователь успешно создан");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        Long userId = user.getId();
        if (userId == null)
            throw new ValidationException("Id не может быть пустым");
        if (!usersInfo.containsKey(userId))
            throw new NotFoundException(String.format("Пользователь с id = %d, не существует", userId));
        User userToUpdate = usersInfo.get(userId);
        userToUpdate.setBirthday(user.getBirthday());
        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setLogin(user.getLogin());
        usersInfo.put(userId, userToUpdate);
        log.info("информация о пользователе успешно обновлена");
        return userToUpdate;
    }

    private long getNextId() {
        long maxId = usersInfo.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++maxId;
    }
}