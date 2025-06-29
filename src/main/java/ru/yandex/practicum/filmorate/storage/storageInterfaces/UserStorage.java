package ru.yandex.practicum.filmorate.storage.storageInterfaces;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository
public abstract class UserStorage implements BaseStorage<User> {
    public abstract User modify(UserDto userDto);

    public abstract User addFriend(final Long userId, final Long friendId);

    public abstract User removeFriend(final Long userId, final Long friendId);

    public abstract Collection<User> findCommonFriends(final Long userId, final Long otherUserId);
}
