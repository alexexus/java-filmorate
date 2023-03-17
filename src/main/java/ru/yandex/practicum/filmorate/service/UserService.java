package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserDao;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private int generatorId = 0;
    private final UserDao userDao;

    @Autowired
    public UserService(@Qualifier("userDaoImpl") UserDao userDao) {
        this.userDao = userDao;
    }

    public void addFriend(long id, long otherId) {
        if (!userDao.userExists(id) || !userDao.userExists(otherId)) {
            throw new NotFoundException("User not found");
        }
        userDao.addFriend(id, otherId);
    }

    public void deleteFriend(long id, long otherId) {
        if (!userDao.userExists(id) || !userDao.userExists(otherId)) {
            throw new NotFoundException("User not found");
        }
        userDao.deleteFriend(id, otherId);
    }

    public List<User> getAllFriends(long id) {
        if (!userDao.userExists(id)) {
            throw new NotFoundException("User not found");
        }
        return userDao.getAllFriends(id);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        if (!userDao.userExists(id) || !userDao.userExists(otherId)) {
            throw new NotFoundException("User not found");
        }
        return userDao.getCommonFriends(id, otherId);
    }

    public User addUser(User user) {
        validate(user);
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++generatorId);
        log.debug("Added new user {}", user);
        return userDao.addUser(user);
    }

    public User updateUser(User user) {
        if (!userDao.userExists(user.getId())) {
            throw new NotFoundException("User not found");
        }
        validate(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        log.debug("User {} updated", user);
        return userDao.updateUser(user);
    }

    public void deleteUser(long id) {
        if (!userDao.userExists(id)) {
            throw new NotFoundException("User not found");
        }
        log.debug("User {} deleted", userDao.getUserById(id));
        userDao.deleteUser(id);
    }

    public User getUserById(long id) {
        if (!userDao.userExists(id)) {
            throw new NotFoundException("User not found");
        }
        return userDao.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot contain spaces");
        }
    }
}
