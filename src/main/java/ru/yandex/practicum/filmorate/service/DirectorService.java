package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao, FilmDao filmDao) {
        this.directorDao = directorDao;
    }

    public Director getDirectorById(long id) {
        if (!directorDao.directorExists(id)) {
            throw new NotFoundException("Director not found");
        }
        return directorDao.getDirectorById(id);
    }

    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public Director addDirector(Director director) {
        validate(director);
        return directorDao.addDirector(director);
    }

    public Director updateDirector(Director director) {
        if (!directorDao.directorExists(director.getId())) {
            throw new NotFoundException("Director not found");
        }
        return directorDao.updateDirector(director);
    }

    public void deleteDirector(long id) {
        if (!directorDao.directorExists(id)) {
            throw new NotFoundException("Director not found");
        }
        directorDao.deleteDirector(id);
    }

    private void validate(Director director) {
        if (director.getName().isBlank() || director.getName().isEmpty()) {
            throw new ValidationException("Director name is empty or blank");
        }
    }
}
