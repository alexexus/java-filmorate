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
        if (userStorage.getUserById(otherId) == null) {
            throw new NotFoundException("Wrong id");
        }
        getUserById(id).getFriends().add(otherId);
        getUserById(otherId).getFriends().add(id);
    }

    public void deleteFriend(Integer id, Integer otherId) {
        if (userStorage.getUserById(otherId) == null) {
            throw new NotFoundException("Wrong id");
        }
        getUserById(id).getFriends().remove(otherId);
        getUserById(otherId).getFriends().remove(id);
    }

    public List<User> getAllFriends(Integer id) {
        return getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<Integer> userFriends = new ArrayList<>(getUserById(id).getFriends());
        userFriends.retainAll(getUserById(otherId).getFriends());
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
        User userById = userStorage.getUserById(id);
        if (userById == null) {
            throw new NotFoundException("Wrong id");
        }
        log.debug("User {} deleted", userById);
        userStorage.deleteUser(id);
    }

    public User getUserById(Integer id) {
        User userById = userStorage.getUserById(id);
        if (userById == null) {
            throw new NotFoundException("Wrong id");
        }
        return userById;
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
