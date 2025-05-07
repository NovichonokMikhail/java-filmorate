package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> filmsInfo = new HashMap<>();

    @Override
    public void add(Film film) {
        filmsInfo.put(film.getId(), film);
    }

    @Override
    public void remove(Film film) {
        filmsInfo.remove(film.getId());
    }

    @Override
    public void modify(Film film) {
        filmsInfo.put(film.getId(), film);
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
}
