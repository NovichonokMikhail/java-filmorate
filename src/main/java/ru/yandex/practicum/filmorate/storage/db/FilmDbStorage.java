package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.RatingStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Primary
@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage extends FilmStorage {
    private final JdbcTemplate jdbc;

    private final FilmRowMapper filmMapper;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;
    private static final String SELECT_ALL_FILMS = "SELECT * FROM film";
    private static final String SELECT_FILM_BY_ID = "SELECT * FROM film WHERE film_id = ?";

    @Override
    public Film add(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String query = "INSERT INTO film (name, description, release_date, duration, rating_id) " +
                " VALUES (?, ?, ?, ?, ?)";
        if (!ratingStorage.exists(film.getMpa().getId()))
            throw new NotFoundException("Rating does not exist");
        boolean anyGenreDoesNotExist = !film.getGenres().stream()
                .map(Genre::getId)
                .allMatch(genreStorage::exists);
        if (anyGenreDoesNotExist)
            throw new NotFoundException("At least one of genres does not exist");
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        final Number id = keyHolder.getKey();
        if (id != null) {
            final Long filmId = id.longValue();
            log.info("film added successfully");
            addFilmGenres(filmId, film.getGenres());
            return get(filmId);
        }
        throw new InternalError("Ошибка сохранения данных");
    }

    @Override
    public boolean remove(Long filmId) {
        final String queryFilm = "DELETE FROM film WHERE film_id = ?";
        return jdbc.update(queryFilm, filmId) > 0;
    }

    @Override
    public Film modify(FilmDto filmDto) {
        final Long filmId = filmDto.getId();
        Film film = get(filmId);
        if (filmDto.hasReleaseDate())
            film.setReleaseDate(filmDto.getReleaseDate());
        if (filmDto.hasName())
            film.setName(filmDto.getName());
        if (filmDto.hasDescription())
            film.setDescription(filmDto.getDescription());
        if (filmDto.hasDuration())
            film.setDuration(filmDto.getDuration());
        final String query = "UPDATE film SET release_date = ?, name = ?, description = ?, duration = ? WHERE film_id = ?";
        jdbc.update(query, film.getReleaseDate(), film.getName(), film.getDescription(), film.getDuration(), filmId);
        return get(filmId);
    }

    @Override
    public Film get(Long filmId) {
        Film film = jdbc.queryForObject(SELECT_FILM_BY_ID, filmMapper, filmId);
        film.setGenres(getGenres(filmId));
        film.setMpa(getFilmRating(filmId));
        film.setLikedUsersIds(
                new HashSet<>(jdbc.query("SELECT user_id FROM user_likes WHERE film_id = ? ORDER BY user_id",
                        (rs, rowNum) -> rs.getLong("user_id"), filmId))
        );
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        List<Film> films = jdbc.query(SELECT_ALL_FILMS, filmMapper);
        for (Film film : films) {
            film.setGenres(getGenres(film.getId()));
            film.setMpa(getFilmRating(film.getId()));
            film.setLikedUsersIds(
                    new HashSet<>(jdbc.query("SELECT user_id FROM user_likes WHERE film_id = ?",
                            (rs, rowNum) -> rs.getLong("user_id"), film.getId()))
            );
        }
        return films;
    }

    @Override
    public boolean exists(final Long filmId) {
        return !jdbc.query(SELECT_FILM_BY_ID, filmMapper, filmId).isEmpty();
    }

    @Override
    public Film addLike(Long userId, Long filmId) {
        final String query = "INSERT INTO user_likes (user_id, film_id) VALUES (?, ?)";
        jdbc.update(query, userId, filmId);
        return get(filmId);
    }

    @Override
    public Film removeLike(Long userId, Long filmId) {
        final String query = "DELETE FROM user_likes WHERE user_id = ? AND film_id = ?";
        jdbc.update(query, userId, filmId);
        return get(filmId);
    }

    private void addFilmGenres(final Long film_id, Set<Genre> genres) {
        if (genres.isEmpty())
            return;
        final String query = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        genres.stream()
                .filter(g -> genreStorage.exists(g.getId()))
                .forEach(genre -> jdbc.update(query, film_id, genre.getId()));
        log.debug("Genres added successfully");
    }

    private Set<Genre> getGenres(Long filmId) {
        List<Genre> genres = jdbc.query("SELECT genre_id FROM film_genre WHERE film_id = ? ORDER BY genre_id",
                (rs, rowNum) -> rs.getLong("genre_id"), filmId)
                .stream()
                .map(genreStorage::get)
                .toList();
        Set<Genre> genreSet = new TreeSet<>(Comparator.comparingLong(Genre::getId));
        genreSet.addAll(genres);
        return genreSet;
    }

    private RatingMPA getFilmRating(final Long filmId) {
        Long ratingId = jdbc.queryForObject("SELECT rating_id FROM film WHERE film_id = ?",
                (rs, rowNum) -> rs.getLong("rating_id"), filmId);
        return ratingStorage.get(ratingId);
    }
}
