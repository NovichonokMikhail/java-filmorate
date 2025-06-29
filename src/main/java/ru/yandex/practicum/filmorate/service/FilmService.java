package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film findById(Long id) {
        if (filmStorage.exists(id))
            return filmStorage.get(id);
        throw new NotFoundException("Film was not found");
    }

    public Film create(Film film) {
        log.debug("фильм добавлен");
        return filmStorage.add(film);
    }

    public Film update(FilmDto filmDto) {
        final Long filmId = filmDto.getId();
        if (!filmStorage.exists(filmId))
            throw new NotFoundException(String.format("Фильма с id = %d, не существует", filmId));
        return filmStorage.modify(filmDto);
    }

    public Collection<Film> findPopular(int size) {
        if (size <= 0)
            throw new ValidationException("Некорректный размер выборки. Должен быть больше чем 0");
        return findAll()
                .stream()
                .sorted(Comparator.comparingLong((Film f) -> f.getLikedUsersIds().size()).reversed())
                .limit(size)
                .toList();
    }

    public Film likeFilm(Long filmId, Long userId) {
        if (!filmStorage.exists(filmId))
            throw new NotFoundException("Film does not exist");
        Film film = findById(filmId);
        if (!userStorage.exists(userId))
            throw new NotFoundException("User does not exist");
        Set<Long> likedUsersIds = film.getLikedUsersIds();
        if (likedUsersIds.contains(userId))
            throw new ValidationException("Пользователь уже лайкнул этот фильм");
        return filmStorage.addLike(userId, filmId);
    }

    public Film removeLike(Long filmId, Long userId) {
        if (!filmStorage.exists(filmId))
            throw new NotFoundException("Film does not exist");
        Film film = findById(filmId);
        if (!userStorage.exists(userId))
            throw new NotFoundException("User does not exist");
        Set<Long> likedUsersIds = film.getLikedUsersIds();
        if (!likedUsersIds.contains(userId))
            throw new ValidationException("Пользователь еще не лайкнул этот фильм");
        return filmStorage.removeLike(userId, filmId);
    }
}