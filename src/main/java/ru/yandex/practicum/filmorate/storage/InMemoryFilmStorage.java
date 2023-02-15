package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int generatorId = 0;

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        validate(film);
        film.setId(++generatorId);
        films.put(film.getId(), film);
        log.debug("Added new film {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Film {} updated", film);
        } else {
            throw new NotFoundException("Wrong id");
        }
        log.debug("Film {} updated", film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteFilm(int id) {
        if (films.containsKey(id)) {
            log.debug("Film {} deleted", films.get(id));
            films.remove(id);
        } else {
            throw new NotFoundException("Wrong id");
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Wrong id");
        }
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Release date - no earlier than December 28, 1895");
            throw new ValidationException("Release date - no earlier than December 28, 1895");
        }
    }
}
