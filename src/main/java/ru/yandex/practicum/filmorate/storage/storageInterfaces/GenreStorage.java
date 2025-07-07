package ru.yandex.practicum.filmorate.storage.storageInterfaces;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

@Repository
public abstract class GenreStorage implements BaseStorage<Genre> {
}
