package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<RatingMPA> {
    @Override
    public RatingMPA mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RatingMPA(rs.getLong("rating_id"), rs.getString("rating_name"));
    }
}
