package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private int generatorId = 0;

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer id, Integer otherId) {
        if (userStorage.getUserById(id) == null || otherId <= 0) {
            throw new NotFoundException("Id's must be positive");
        }
        userStorage.getUserById(id).getFriends().add(otherId);
        userStorage.getUserById(otherId).getFriends().add(id);
    }

    public void deleteFriend(Integer id, Integer otherId) {
        if (userStorage.getUserById(id) == null || otherId <= 0) {
            throw new NotFoundException("Id's must be positive");
        }
        userStorage.getUserById(id).getFriends().remove(otherId);
        userStorage.getUserById(otherId).getFriends().remove(id);
    }

    public List<User> getAllFriends(Integer id) {
        return userStorage.getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<Integer> userFriends = new ArrayList<>(userStorage.getUserById(id).getFriends());
        userFriends.retainAll(userStorage.getUserById(otherId).getFriends());
        return userFriends.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public User addUser(User user) {
        validate(user);
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++generatorId);
        log.debug("Added new user {}", user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.getUserById(user.getId()) == null) {
            throw new NotFoundException("Wrong id");
        }
        validate(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        log.debug("User {} updated", user);
        return userStorage.updateUser(user);
    }

    public void deleteUser(Integer id) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException("Wrong id");
        }
        log.debug("User {} deleted", userStorage.getUserById(id));
        userStorage.deleteUser(id);
    }

    public User getUserById(Integer id) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException("Wrong id");
        }
        return userStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot contain spaces");
        }
    }
}
