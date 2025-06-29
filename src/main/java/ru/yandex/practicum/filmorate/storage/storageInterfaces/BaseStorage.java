package ru.yandex.practicum.filmorate.storage.storageInterfaces;

import java.util.Collection;

public interface BaseStorage<T> {
    T add(T obj);

    boolean remove(Long objId);

    T get(Long id);

    Collection<T> getAll();

    boolean exists(Long objId);
}
