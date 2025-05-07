package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.MovieReleaseDate;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    @Size(max = 200)
    String description;
    @MovieReleaseDate
    LocalDate releaseDate;
    @Positive
    Long duration;
    long likes;
}