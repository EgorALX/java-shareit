package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    User updateUser(Long id, User user);

    Optional<User> getById(Long id);

    void removeById(Long id);

    List<User> getUsers();
}
