package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.MovieReleaseDate;

import java.time.LocalDate;

@Data
public class FilmDto {
    @NotNull
    private Long id;
    private String name;
    @Positive
    private Long duration;
    @Size(max = 200)
    private String description;
    @MovieReleaseDate
    private LocalDate releaseDate;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }
}
