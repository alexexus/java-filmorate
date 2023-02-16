package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User addUser(User user);

    void deleteUser(Integer id);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(Integer id);

    Map<Integer, User> getUsers();
}
