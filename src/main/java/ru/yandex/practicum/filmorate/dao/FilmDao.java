package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {

    Film addFilm(Film film);

    void deleteFilm(long id);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(long id);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    List<Film> getPopularFilms(long count);

    List<Film> getSortedFilmsByDirId(long directorId, String sort);

    boolean filmExists(long id);
}
