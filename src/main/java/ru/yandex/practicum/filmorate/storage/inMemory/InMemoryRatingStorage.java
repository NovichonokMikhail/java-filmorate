package ru.yandex.practicum.filmorate.storage.inMemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.RatingStorage;

import java.util.Collection;
import java.util.Map;

@Component
public class InMemoryRatingStorage extends RatingStorage {
    private Map<Long, RatingMPA> ratings = Map.of(
            1L, new RatingMPA(1L, "G"),
            2L, new RatingMPA(2L, "PG"),
            3L, new RatingMPA(3L, "PG-13"),
            4L, new RatingMPA(4L, "R"),
            5L, new RatingMPA(5L, "NC-17"));

    @Override
    public RatingMPA add(RatingMPA obj) {
        return ratings.put(obj.getId(), obj);
    }

    @Override
    public boolean remove(Long objId) {
        return ratings.remove(objId) != null;
    }

    @Override
    public RatingMPA get(Long id) {
        if (exists(id))
            return ratings.get(id);
        throw new NotFoundException("Rating Does not exist");
    }

    @Override
    public Collection<RatingMPA> getAll() {
        return ratings.values();
    }

    @Override
    public boolean exists(Long objId) {
        return ratings.get(objId) != null;
    }
}
