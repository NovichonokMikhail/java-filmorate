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
    public Collection<User> getUser() {
        log.info("getting all users");
        return usersInfo.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("trying to create a user");
        if (user.getName() == null)
            user.setName(user.getLogin());
        user.setId(getNextId());
        usersInfo.put(user.getId(), user);
        log.info("user created successfully");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        Long userId = user.getId();
        if (userId == null) {
            log.error("user was not found");
            throw new ValidationException("Id не может быть пустым");
        }
        if (!usersInfo.containsKey(userId))
            throw new NotFoundException(String.format("Пользователь с id = %d, не существует", userId));
        User userToUpdate = usersInfo.get(userId);
        if (user.getBirthday() != null)
            userToUpdate.setBirthday(user.getBirthday());
        if (user.getName() != null)
            userToUpdate.setName(user.getName());
        if (user.getEmail() != null)
            userToUpdate.setEmail(user.getEmail());
        if (user.getLogin() != null)
            userToUpdate.setLogin(user.getLogin());
        usersInfo.put(userId, userToUpdate);
        log.info("user successfully updated");
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