package ru.yandex.practicum.filmorate.storage.inMemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.GenreStorage;

import java.util.Collection;
import java.util.Map;

@Component
public class InMemoryGenreStorage extends GenreStorage {
    private Map<Long, Genre> genres = Map.of(
            1L, new Genre(1L, "Комедия"),
            2L, new Genre(2L, "Драма"),
            3L, new Genre(3L, "Мультфильм"),
            4L, new Genre(4L, "Триллер"),
            5L, new Genre(5L, "Документальный"),
            6L, new Genre(6L, "Боевик")
    );

    @Override
    public Genre add(Genre genre) {
        return genres.put(genre.getId(), genre);
    }

    @Override
    public boolean remove(Long genreId) {
        return genres.remove(genreId) != null;
    }

    @Override
    public Genre get(Long id) {
        if (exists(id))
            return genres.get(id);
        throw new NotFoundException("Genre does not exist");
    }

    @Override
    public Collection<Genre> getAll() {
        return genres.values();
    }

    @Override
    public boolean exists(Long objId) {
        return genres.get(objId) != null;
    }
}
