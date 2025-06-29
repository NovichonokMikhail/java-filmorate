package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.storageInterfaces.RatingStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class RatingController {
    private final RatingStorage ratingStorage;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<RatingMPA> getGenres() {
        return ratingStorage.getAll();
    }

    @GetMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public RatingMPA getById(@PathVariable final Long ratingId) {
        if (ratingStorage.exists(ratingId))
            return ratingStorage.get(ratingId);
        throw new NotFoundException("Rating does not exist");
    }
}