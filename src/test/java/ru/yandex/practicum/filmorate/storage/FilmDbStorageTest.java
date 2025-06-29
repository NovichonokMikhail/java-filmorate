package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.RatingRowMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, RatingDbStorage.class,
        GenreRowMapper.class, RatingRowMapper.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    Film getTestFilm() {
        Film film = new Film();
        film.setName("Test Film");
        Genre genre = new Genre();
        genre.setId(1L);
        film.setGenres(Set.of(genre));
        film.setDuration(120L);
        RatingMPA rating = new RatingMPA();
        rating.setId(1L);
        film.setMpa(rating);
        film.setDescription("test film description");
        film.setReleaseDate(LocalDate.now());
        return film;
    }

    List<Film> getNTestFilms(int n) {
        List<Film> films = new ArrayList<>();
        for (long i = 1; i <= n; i++) {
            Film film = new Film();
            Set<Genre> genres = LongStream.rangeClosed(1, Math.min(5, n)).boxed()
                            .map(id -> {
                                Genre genre = new Genre();
                                genre.setId(id);
                                return genre;
                            }
                            ).collect(Collectors.toSet());
            RatingMPA ratingMPA = new RatingMPA();
            ratingMPA.setId(3L);
            film.setName("Test Film #" + i);
            film.setGenres(genres);
            film.setDuration(120L + i * 20);
            film.setMpa(ratingMPA);
            film.setDescription("test film description");
            film.setReleaseDate(LocalDate.now().minusWeeks(i));
            films.add(film);
        }
        return films;
    }

    @Test
    void canCreateFilm() {
        Film film = getTestFilm();
        Film createdFilm = filmStorage.add(film);
        System.out.println(createdFilm.getId());
        assert createdFilm.getId() == 1;
    }

    @Test
    void canGetFilmById() {
        Film film = filmStorage.add(getTestFilm());
        assert filmStorage.get(1L).equals(film);
    }

    @Test
    void canGetAllFilms() {
        System.out.println(filmStorage.getAll().size());
        List<Film> createdFilms = getNTestFilms(5).stream()
                .map(filmStorage::add)
                .toList();
        List<Film> getAllFilms = new ArrayList<>(filmStorage.getAll());
        System.out.println(getAllFilms.size());
        assert !filmStorage.getAll().isEmpty() : "Cannot be empty";
        assert getAllFilms.size() == 5;
        assert createdFilms.equals(getAllFilms);
    }

    @Test
    void canDeleteFilm() {
        Film film = filmStorage.add(getTestFilm());
        assert filmStorage.getAll().size() == 1;
        assert filmStorage.remove(film.getId());
        assert filmStorage.getAll().isEmpty();
    }
}

