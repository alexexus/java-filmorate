package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private int generatorId = 0;

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        validate(user);
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++generatorId);
        users.put(user.getId(), user);
        log.debug("Added new user {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validate(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("User {} updated", user);
        } else {
            throw new NotFoundException("Wrong id");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Integer id) {
        if (users.containsKey(id)) {
            log.debug("User {} deleted", users.get(id));
            users.remove(id);
        } else {
            throw new NotFoundException("Wrong id");
        }
    }

    @Override
    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Wrong id");
        }
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Login cannot contain spaces");
            throw new ValidationException("Login cannot contain spaces");
        }
    }
}
