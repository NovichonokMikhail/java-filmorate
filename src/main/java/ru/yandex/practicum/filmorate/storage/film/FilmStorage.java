package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void add(Film film);

    void remove(Film film);

    void modify(Film film);

    Film get(Long id);

    Collection<Film> getAll();
}
