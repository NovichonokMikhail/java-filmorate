package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    public static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);
    private final HashMap<Long, Film> filmsInfo = new HashMap<>();

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmsInfo.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        filmsInfo.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        final Long filmId = film.getId();
        if (filmId == null)
            throw new ValidationException("Id должно быть указано");
        if (!filmsInfo.containsKey(filmId))
            throw new NotFoundException(String.format("Фильма с id = %d, не существует", filmId));
        Film filmToUpdate = filmsInfo.get(filmId);
        if (film.getName() != null)
            filmToUpdate.setName(film.getName());
        if (film.getDescription() != null && film.getDescription().length() <= 200)
            filmToUpdate.setDescription(film.getDescription());
        if (film.getReleaseDate().isAfter(EARLIEST_DATE))
            filmToUpdate.setReleaseDate(film.getReleaseDate());
        if (film.getName() != null)
            filmToUpdate.setName(film.getName());
        if (film.getDuration() != null)
            filmToUpdate.setDuration(film.getDuration());
        filmsInfo.put(filmId, filmToUpdate);
        return filmToUpdate;
    }

    private long getNextId() {
        long maxId = filmsInfo.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
