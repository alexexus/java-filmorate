package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {

    private final MpaDaoImpl mpaDaoImpl;

    @Autowired
    public MpaService(MpaDaoImpl mpaDaoImpl) {
        this.mpaDaoImpl = mpaDaoImpl;
    }

    public Mpa getMpaById(long id) {
        if (!mpaDaoImpl.mpaExists(id)) {
            throw new NotFoundException("Wrong id");
        }
        return mpaDaoImpl.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaDaoImpl.getAllMpa();
    }
}
