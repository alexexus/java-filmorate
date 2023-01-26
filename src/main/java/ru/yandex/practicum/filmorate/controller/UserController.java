package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
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
        validate(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(++generatorId);
        users.put(user.getId(), user);
        log.debug("Added new user {}", user);
        return user;
    }

    @PutMapping("/users")
    public User updateFilm(@Valid @RequestBody User user) {
        validate(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        User userToUpdate = users.get(user.getId());
        userToUpdate.setBirthday(user.getBirthday());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setName(user.getName());
        userToUpdate.setLogin(user.getLogin());
        log.debug("User {} updated", user);
        return user;
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Login cannot contain spaces");
            throw new ValidationException("Login cannot contain spaces");
        }
    }
}
