package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film findById(Long id) {
        return filmStorage.get(id);
    }

    public Film create(Film film) {
        film.setId(getNextId());
        filmStorage.add(film);
        log.info("фильм добавлен");
        return film;
    }

    public Film update(Film film) {
        final Long filmId = film.getId();
        if (filmId == null)
            throw new ValidationException("Id должно быть указано");
        if (filmStorage.get(filmId) == null)
            throw new NotFoundException(String.format("Фильма с id = %d, не существует", filmId));
        Film filmToUpdate = filmStorage.get(filmId);
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDuration(film.getDuration());
        log.info("Информация о фильме успешно обновлена");
        filmStorage.modify(filmToUpdate);
        return film;
    }

    public Collection<Film> findPopular(int size) {
        if (size <= 0)
            throw new ValidationException("Некорректный размер выборки. Должен быть больше чем 0");
        return findAll()
                .stream()
                .sorted(Comparator.comparingLong(Film::getLikes).reversed())
                .limit(size)
                .toList();
    }

    public Film likeFilm(Long filmId, Long userId) {
        Film film = findById(filmId);
        Set<Long> likedUsersIds = film.getLikedUsersIds();
        if (likedUsersIds.contains(userId))
            throw new ValidationException("Пользователь уже лайкнул этот фильм");
        likedUsersIds.add(userId);
        film.setLikes(film.getLikes() + 1);
        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        Set<Long> likedUsersIds = film.getLikedUsersIds();
        if (!likedUsersIds.contains(userId))
            throw new ValidationException("Пользователь не лайкнул этот фильм");
        likedUsersIds.remove(userId);
        film.setLikes(film.getLikes() - 1);
        return film;
    }

    private long getNextId() {
        long maxId = filmStorage.getAll().stream()
                .map(Film::getId)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return ++maxId;
    }
}