package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDao {

    Director getDirectorById(long id);

    List<Director> getAllDirectors();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long id);

    boolean directorExists(long id);
}
