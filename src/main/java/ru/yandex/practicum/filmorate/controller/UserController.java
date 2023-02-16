package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id,
                                       @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{otherId}")
    public void addFriend(@PathVariable Integer id,
                          @PathVariable Integer otherId) {
        userService.addFriend(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getAllFriends(id);
    }

    @DeleteMapping("/{id}/friends/{otherId}")
    public void deleteFriend(@PathVariable Integer id,
                             @PathVariable Integer otherId) {
        userService.deleteFriend(id, otherId);
    }
}
