package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Long, Film> filmsInfo = new HashMap<>();

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Получение всех пользователей");
        return filmsInfo.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        filmsInfo.put(film.getId(), film);
        log.info("фильм добавлен");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        final Long filmId = film.getId();
        if (filmId == null)
            throw new ValidationException("Id должно быть указано");
        if (!filmsInfo.containsKey(filmId))
            throw new NotFoundException(String.format("Фильма с id = %d, не существует", filmId));
        Film filmToUpdate = filmsInfo.get(filmId);
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDuration(film.getDuration());
        log.info("Информация о фильме успешно обновлена");
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
