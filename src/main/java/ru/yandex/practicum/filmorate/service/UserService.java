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
        if (id > 0 && otherId > 0) {
            userStorage.getUserById(id).getFriends().add(otherId);
            userStorage.getUserById(otherId).getFriends().add(id);
        } else {
            throw new NotFoundException("Id's must be positive");
        }
    }

    public void deleteFriend(Integer id, Integer otherId) {
        if (id > 0 && otherId > 0) {
            userStorage.getUserById(id).getFriends().remove(otherId);
            userStorage.getUserById(otherId).getFriends().remove(id);
        } else {
            throw new NotFoundException("Id's must be positive");
        }
    }

    public List<User> getAllFriends(Integer id) {
        List<User> usersFriends = new ArrayList<>();
        for (Integer i : userStorage.getUserById(id).getFriends()) {
            usersFriends.add(userStorage.getUserById(i));
        }
        return usersFriends;
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<Integer> idUserFriends = new ArrayList<>(userStorage.getUserById(id).getFriends());
        List<Integer> otherIdUserFriends = new ArrayList<>(userStorage.getUserById(otherId).getFriends());
        List<User> commonFriends = new ArrayList<>();
        for (Integer i : idUserFriends) {
            for (Integer j : otherIdUserFriends) {
                if (i.equals(j)) {
                    commonFriends.add(userStorage.getUserById(i));
                }
            }
        }
        return commonFriends;
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
        validate(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (userStorage.getUsers().containsKey(user.getId())) {
            log.debug("User {} updated", user);
            return userStorage.updateUser(user);
        } else {
            throw new NotFoundException("Wrong id");
        }
    }

    public void deleteUser(Integer id) {
        if (userStorage.getUsers().containsKey(id)) {
            log.debug("User {} deleted", userStorage.getUsers().get(id));
            userStorage.deleteUser(id);
        } else {
            throw new NotFoundException("Wrong id");
        }
    }

    public User getUserById(Integer id) {
        if (userStorage.getUsers().containsKey(id)) {
            return userStorage.getUserById(id);
        } else {
            throw new NotFoundException("Wrong id");
        }
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Login cannot contain spaces");
            throw new ValidationException("Login cannot contain spaces");
        }
    }
}
