package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {

    User addUser(User user);

    void deleteUser(long id);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(long id);

    void addFriend(long id, long otherId);

    void deleteFriend(long id, long otherId);

    List<User> getAllFriends(long id);

    List<User> getCommonFriends(long id, long otherId);

    boolean userExists(long id);
}
