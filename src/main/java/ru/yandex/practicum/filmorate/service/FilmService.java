package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private int generatorId = 0;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long id, long userId) {
        if (filmStorage.filmNotExists(id) || userStorage.userNotExists(userId)) {
            throw new NotFoundException("Wrong id");
        }
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(long id, long userId) {
        if (filmStorage.filmNotExists(id) || userStorage.userNotExists(userId)) {
            throw new NotFoundException("Wrong id");
        }
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getPopularFilms(long count) {
        return filmStorage.getPopularFilms(count);
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
        if (filmStorage.filmNotExists(film.getId())) {
            throw new NotFoundException("Film not found");
        }
        validate(film);
        log.debug("Film {} updated", film);
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(long id) {
        if (filmStorage.filmNotExists(id)) {
            throw new NotFoundException("Film not found");
        }
        log.debug("Film {} deleted", filmStorage.getFilmById(id));
        filmStorage.deleteFilm(id);
    }

    public Film getFilmById(long id) {
        if (filmStorage.filmNotExists(id)) {
            throw new NotFoundException("Film not found");
        }
        return filmStorage.getFilmById(id);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date - no earlier than December 28, 1895");
        }
    }
}
