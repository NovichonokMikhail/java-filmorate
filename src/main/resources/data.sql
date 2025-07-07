INSERT INTO genre (genre_id, name)
SELECT * FROM (VALUES
        (1, 'Комедия'),
        (2, 'Драма'),
        (3, 'Мультфильм'),
        (4, 'Триллер'),
        (5, 'Документальный'),
        (6, 'Боевик')
        ) AS new_genre(genre_id, name)
WHERE NOT EXISTS (
    SELECT 1 FROM genre
    WHERE genre.genre_id = new_genre.genre_id
);

INSERT INTO mpa_rating (rating_id, rating_name)
SELECT * FROM (VALUES
        (1, 'G'),
        (2, 'PG'),
        (3, 'PG-13'),
        (4, 'R'),
        (5, 'NC-17')
        ) AS new_rating(rating_id, rating_name)
WHERE NOT EXISTS (
    SELECT 1 FROM mpa_rating
    WHERE mpa_rating.rating_id = new_rating.rating_id
);