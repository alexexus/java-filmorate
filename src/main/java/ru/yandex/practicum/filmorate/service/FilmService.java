package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private int generatorId = 0;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer id, Integer userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Wrong id");
        }
        getFilmById(id).getLikes().add(userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Wrong id");
        }
        getFilmById(id).getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return getAllFilms().stream()
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
        if (filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException("Wrong id");
        }
        validate(film);
        log.debug("Film {} updated", film);
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Integer id) {
        Film filmById = filmStorage.getFilmById(id);
        if (filmById == null) {
            throw new NotFoundException("Wrong id");
        }
        log.debug("Film {} deleted", filmById);
        filmStorage.deleteFilm(id);
    }

    public Film getFilmById(Integer id) {
        Film filmById = filmStorage.getFilmById(id);
        if (filmById == null) {
            throw new NotFoundException("Wrong id");
        }
        return filmById;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date - no earlier than December 28, 1895");
        }
    }
}
