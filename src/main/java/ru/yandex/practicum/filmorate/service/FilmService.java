package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private int generatorId = 0;

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Integer id, Integer userId) {
        if (id > 0 && userId > 0) {
            filmStorage.getFilmById(id).getLikes().add(userId);
        } else {
            throw new NotFoundException("Id's must be positive");
        }
    }

    public void deleteLike(Integer id, Integer userId) {
        if (id > 0 && userId > 0) {
            filmStorage.getFilmById(id).getLikes().remove(userId);
        } else {
            throw new NotFoundException("Id's must be positive");
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        validate(film);
        film.setId(++generatorId);
        log.debug("Added new film {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        if (filmStorage.getFilms().containsKey(film.getId())) {
            log.debug("Film {} updated", film);
            return filmStorage.updateFilm(film);
        } else {
            throw new NotFoundException("Wrong id");
        }
    }

    public void deleteFilm(Integer id) {
        if (filmStorage.getFilms().containsKey(id)) {
            log.debug("Film {} deleted", filmStorage.getFilms().get(id));
            filmStorage.deleteFilm(id);
        } else {
            throw new NotFoundException("Wrong id");
        }
    }

    public Film getFilmById(Integer id) {
        if (filmStorage.getFilms().containsKey(id)) {
            return filmStorage.getFilmById(id);
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
