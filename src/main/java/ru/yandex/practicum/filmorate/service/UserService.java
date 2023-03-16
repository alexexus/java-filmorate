package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private int generatorId = 0;

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long id, long otherId) {
        if (userStorage.userNotExists(id) || userStorage.userNotExists(otherId)) {
            throw new NotFoundException("User not found");
        }
        userStorage.addFriend(id, otherId);
    }

    public void deleteFriend(long id, long otherId) {
        if (userStorage.userNotExists(id) || userStorage.userNotExists(otherId)) {
            throw new NotFoundException("User not found");
        }
        userStorage.deleteFriend(id, otherId);
    }

    public List<User> getAllFriends(long id) {
        if (userStorage.userNotExists(id)) {
            throw new NotFoundException("User not found");
        }
        return userStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        if (userStorage.userNotExists(id) || userStorage.userNotExists(otherId)) {
            throw new NotFoundException("User not found");
        }
        return userStorage.getCommonFriends(id, otherId);
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
        if (userStorage.userNotExists(user.getId())) {
            throw new NotFoundException("User not found");
        }
        validate(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        log.debug("User {} updated", user);
        return userStorage.updateUser(user);
    }

    public void deleteUser(long id) {
        if (userStorage.userNotExists(id)) {
            throw new NotFoundException("User not found");
        }
        log.debug("User {} deleted", userStorage.getUserById(id));
        userStorage.deleteUser(id);
    }

    public User getUserById(long id) {
        if (userStorage.userNotExists(id)) {
            throw new NotFoundException("User not found");
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
