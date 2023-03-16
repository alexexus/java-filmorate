package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(@Qualifier("mpaDbStorage") MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa getMpaById(long id) {
        if (mpaDbStorage.mpaNotExists(id)) {
            throw new NotFoundException("Wrong id");
        }
        return mpaDbStorage.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

}
