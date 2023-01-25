package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@Slf4j
public class UserController {

    private int generatorId = 0;

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User addFilm(@Valid @RequestBody User user) {
        checkException(user);
        user.setId(++generatorId);
        users.put(user.getId(), user);
        log.trace("Добавлен новый пользователь {}", user);
        return user;
    }

    @PutMapping("/users")
    public User updateFilm(@Valid @RequestBody User user) {
        checkException(user);
        users.get(user.getId()).setBirthday(user.getBirthday());
        users.get(user.getId()).setEmail(user.getEmail());
        users.get(user.getId()).setName(user.getName());
        users.get(user.getId()).setLogin(user.getLogin());
        log.trace("Пользователь {} обновлен", user);
        return user;
    }

    private void checkException(@RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getEmail().isEmpty() || !(user.getEmail().contains("@"))) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }


}
