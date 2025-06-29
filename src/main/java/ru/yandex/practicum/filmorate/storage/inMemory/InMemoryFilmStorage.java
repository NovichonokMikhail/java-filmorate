package ru.yandex.practicum.filmorate.storage.inMemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.FilmStorage;

import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryFilmStorage extends FilmStorage {
    private final HashMap<Long, Film> filmsInfo = new HashMap<>();

    @Override
    public Film add(Film film) {
        film.setId(getNextId());
        return filmsInfo.put(film.getId(), film);
    }

    @Override
    public boolean remove(final Long filmId) {
        return filmsInfo.remove(filmId) != null;
    }

    @Override
    public Film modify(FilmDto filmDto) {

//        filmsInfo.put(film.getId(), film);
//        return filmsInfo.get(film.getId());
        return null;
    }

    @Override
    public Film addLike(Long userId, Long filmId) {
        Film film = get(filmId);
        film.getLikedUsersIds().add(userId);
        return filmsInfo.put(filmId, film);
    }

    @Override
    public Film removeLike(Long userId, Long filmId) {
        Film film = get(filmId);
        film.getLikedUsersIds().remove(userId);
        return filmsInfo.put(filmId, film);
    }

    @Override
    public Film get(Long id) {
        Film film = filmsInfo.get(id);
        if (film != null)
            return film;
        throw new NotFoundException("Фильм с id " + id + " - не найден");
    }

    @Override
    public Collection<Film> getAll() {
        return filmsInfo.values();
    }

    @Override
    public boolean exists(Long filmId) {
        return filmsInfo.get(filmId) != null;
    }

    private long getNextId() {
        long maxId = getAll().stream()
                .map(Film::getId)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
