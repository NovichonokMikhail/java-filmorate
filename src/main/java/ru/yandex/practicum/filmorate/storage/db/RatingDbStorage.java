package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.RatingStorage;

import java.sql.PreparedStatement;
import java.util.Collection;

@Primary
@Repository
@Slf4j
@RequiredArgsConstructor
public class RatingDbStorage extends RatingStorage {
    private final JdbcTemplate jdbc;
    private final RatingRowMapper ratingMapper;
    private static final String SELECT_ALL_RATING = "SELECT * FROM mpa_rating";
    private static final String SELECT_RATING_BY_ID = "SELECT * FROM mpa_rating WHERE rating_id = ?";

    @Override
    public RatingMPA add(RatingMPA rating) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String query = "INSERT INTO mpa_rating (name) VALUES (?)";
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, rating.getName());
            return ps;
        }, keyHolder);
        final Number genreId = keyHolder.getKey();
        if (genreId != null)
            return get(genreId.longValue());
        throw new InternalError("Ошибка сохранения данных");
    }

    @Override
    public boolean remove(Long ratingId) {
        final String query = "DELETE FROM mpa_rating WHERE rating_id = ?";
        return jdbc.update(query, ratingId) > 0;
    }

    @Override
    public RatingMPA get(Long ratingId) {
        if (exists(ratingId))
            return jdbc.queryForObject(SELECT_RATING_BY_ID, ratingMapper, ratingId);
        throw new NotFoundException("Rating was not found");
    }

    @Override
    public Collection<RatingMPA> getAll() {
        return jdbc.query(SELECT_ALL_RATING, ratingMapper);
    }

    @Override
    public boolean exists(final Long ratingId) {
        return !jdbc.query(SELECT_RATING_BY_ID, ratingMapper, ratingId).isEmpty();
    }
}
