package ru.yandex.practicum.filmorate.storage.storageInterfaces;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@Repository
public abstract class FilmStorage implements BaseStorage<Film> {
    public abstract Film modify(FilmDto filmDto);

    public abstract Film addLike(Long userId, Long filmId);

    public abstract Film removeLike(Long userId, Long filmId);
}
