package ru.yandex.practicum.filmorate.storage.storageInterfaces;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.RatingMPA;

@Repository
public abstract class RatingStorage implements BaseStorage<RatingMPA> {
}