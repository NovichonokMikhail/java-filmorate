package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
    String description;
    LocalDate releaseDate;
    Long duration;
}