package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    Long id;
    @NotBlank
    @Email
    String email;
    @NotBlank
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;
    final Set<Long> friendsList = new HashSet<>();
    final Set<Long> likedFilms = new HashSet<>();
}
