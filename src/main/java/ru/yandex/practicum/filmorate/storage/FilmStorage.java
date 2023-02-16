package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Film addFilm(Film film);

    void deleteFilm(Integer id);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Integer id);

    Map<Integer, Film> getFilms();
}
