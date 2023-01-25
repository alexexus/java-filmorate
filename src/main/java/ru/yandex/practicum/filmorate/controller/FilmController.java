package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
        log.trace("Добавлен новый фильм {}", film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        checkException(film);
        films.get(film.getId()).setDescription(film.getDescription());
        films.get(film.getId()).setDuration(film.getDuration());
        films.get(film.getId()).setName(film.getName());
        films.get(film.getId()).setReleaseDate(film.getReleaseDate());
        log.trace("Фильм {} обновлен", film);
        return film;
    }

    private void checkException(@Valid @RequestBody Film film) {
        if (film.getName().isEmpty()) {
            log.error("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

}
