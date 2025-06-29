package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.GenreStorage;

import java.sql.PreparedStatement;
import java.util.Collection;

@Primary
@Repository
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage extends GenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreMapper;
    private static final String SELECT_ALL_GENRES = "SELECT * FROM genre";
    private static final String SELECT_GENRE_BY_ID = "SELECT * FROM genre WHERE genre_id = ?";

    @Override
    public Genre add(Genre genre) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String query = "INSERT INTO genre (name) VALUES (?)";
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, genre.getName());
            return ps;
        }, keyHolder);
        final Number genreId = keyHolder.getKey();
        if (genreId != null)
            return get(genreId.longValue());
        throw new InternalError("Ошибка сохранения данных");
    }

    @Override
    public boolean remove(Long genreId) {
        final String query = "DELETE FROM genre WHERE genre_id = ?";
        return jdbc.update(query, genreId) > 0;
    }

    @Override
    public Genre get(Long genreId) {
        if (exists(genreId))
            return jdbc.queryForObject(SELECT_GENRE_BY_ID, genreMapper, genreId);
        throw new NotFoundException("Genre was not found");
    }

    @Override
    public Collection<Genre> getAll() {
        return jdbc.query(SELECT_ALL_GENRES, genreMapper);
    }

    @Override
    public boolean exists(final Long genreId) {
        return !jdbc.query(SELECT_GENRE_BY_ID, genreMapper, genreId).isEmpty();
    }
}
