package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    public final UserStorage userStorage;

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
}
