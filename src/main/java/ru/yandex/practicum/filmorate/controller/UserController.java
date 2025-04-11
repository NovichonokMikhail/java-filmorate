package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Long, User> usersInfo = new HashMap<>();

    @GetMapping
    public Collection<User> getUser() {
        return usersInfo.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user.getEmail() == null)
            throw new ValidationException("Email не может быть пустым");
        if (!user.getEmail().contains("@"))
            throw new ValidationException("Email не соответсвует требованиям");
        if (user.getLogin() == null || user.getLogin().isBlank())
            throw new ValidationException("Username не может быть пустым и содержать пробелы");
        if (user.getName() == null)
            user.setName(user.getLogin());
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("Дата рождения не может быть в будущем");
        user.setId(getNextId());
        usersInfo.put(user.getId(), user);
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
        if (user.getBirthday() != null)
            userToUpdate.setBirthday(user.getBirthday());
        if (user.getName() != null)
            userToUpdate.setName(user.getName());
        if (user.getEmail() != null)
            userToUpdate.setEmail(user.getEmail());
        if (user.getLogin() != null)
            userToUpdate.setLogin(user.getLogin());
        usersInfo.put(userId, userToUpdate);
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