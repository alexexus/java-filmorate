package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {

    Genre getGenreById(long id);

    List<Genre> getAllGenres();

    List<Genre> getGenresByFilmId(long filmId);

    boolean genreExists(long id);
}
