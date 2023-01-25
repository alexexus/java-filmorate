package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@Slf4j
public class FilmController {

    private int generatorId = 0;

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        checkException(film);
        film.setId(++generatorId);
        films.put(film.getId(), film);
        log.debug("Added new movie {}", film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        checkException(film);
        films.get(film.getId()).setDescription(film.getDescription());
        films.get(film.getId()).setDuration(film.getDuration());
        films.get(film.getId()).setName(film.getName());
        films.get(film.getId()).setReleaseDate(film.getReleaseDate());
        log.debug("Movie {} updated", film);
        return film;
    }

    private void checkException(@Valid @RequestBody Film film) {
        if (film.getName().isEmpty()) {
            log.error("Title cannot be empty");
            throw new ValidationException("Title cannot be empty");
        }
        if (film.getDescription().length() > 200) {
            log.error("The maximum description length is 200 characters");
            throw new ValidationException("The maximum description length is 200 characters");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Release date - no earlier than December 28, 1895");
            throw new ValidationException("Release date - no earlier than December 28, 1895");
        }
    }

}
