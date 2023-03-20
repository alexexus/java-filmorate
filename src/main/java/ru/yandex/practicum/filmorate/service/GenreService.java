package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {

    private final GenreDaoImpl genreDaoImpl;

    @Autowired
    public GenreService(GenreDaoImpl genreDaoImpl) {
        this.genreDaoImpl = genreDaoImpl;
    }

    public Genre getGenreById(long id) {
        if (!genreDaoImpl.genreExists(id)) {
            throw new NotFoundException("Wrong id");
        }
        return genreDaoImpl.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return genreDaoImpl.getAllGenres();
    }
}
