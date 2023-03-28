package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmDao filmDao;
    private final UserDao userDao;

    private final DirectorDao directorDao;

    @Autowired
    public FilmService(FilmDao filmDao, UserDao userDao, DirectorDao directorDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.directorDao = directorDao;
    }

    public void addLike(long id, long userId) {
        if (!filmDao.filmExists(id) || !userDao.userExists(userId)) {
            throw new NotFoundException("Wrong id");
        }
        filmDao.addLike(id, userId);
    }

    public void deleteLike(long id, long userId) {
        if (!filmDao.filmExists(id) || !userDao.userExists(userId)) {
            throw new NotFoundException("Wrong id");
        }
        filmDao.deleteLike(id, userId);
    }

    public List<Film> getPopularFilms(long count) {
        return filmDao.getPopularFilms(count);
    }

    public List<Film> getAllFilms() {
        return filmDao.getAllFilms();
    }

    public Film addFilm(Film film) {
        validate(film);
        log.debug("Added new film {}", film);
        return filmDao.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmDao.filmExists(film.getId())) {
            throw new NotFoundException("Film not found");
        }
        validate(film);
        log.debug("Film {} updated", film);
        return filmDao.updateFilm(film);
    }

    public void deleteFilm(long id) {
        if (!filmDao.filmExists(id)) {
            throw new NotFoundException("Film not found");
        }
        log.debug("Film {} deleted", filmDao.getFilmById(id));
        filmDao.deleteFilm(id);
    }

    public Film getFilmById(long id) {
        if (!filmDao.filmExists(id)) {
            throw new NotFoundException("Film not found");
        }
        return filmDao.getFilmById(id);
    }

    public List<Film> getSortedFilmsByDirId(long directorId, String sort) {
        if (!directorDao.directorExists(directorId)) {
            throw new NotFoundException("Director not found");
        }
        return filmDao.getSortedFilmsByDirId(directorId, sort);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date - no earlier than December 28, 1895");
        }
    }
}
