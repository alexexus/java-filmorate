package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    public final FilmStorage filmStorage;

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
}
