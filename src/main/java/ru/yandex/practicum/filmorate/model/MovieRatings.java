package ru.yandex.practicum.filmorate.model;

public enum MovieRatings {
    G,
    PG,
    PG13,
    R,
    NC17,
    UNRATED;

    public static MovieRatings from(String s) {
        return switch (s) {
            case "g", "G" -> G;
            case "pg", "PG" -> PG;
            case "pg13", "pg-13", "PG-13", "PG13" -> PG13;
            case "r", "R" -> R;
            case "nc17", "NC17", "nc-17", "NC-17" -> NC17;
            default -> UNRATED;
        };
    }
}
