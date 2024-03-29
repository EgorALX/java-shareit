package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImp implements UserStorage {

    private Long id = 1L;
    private final Map<Long, User> usersStorage = new HashMap<>();

    @Override
    public User addUser(User user) {
        for (User userInStorage : usersStorage.values()) {
            if (user.getEmail().equals(userInStorage.getEmail())) {
                throw new DuplicationException("Email already exist");
            }
        }
        Long id = setNewId();
        user.setId(id);
        usersStorage.put(id, user);
        return usersStorage.get(id);
    }

    private Long setNewId() {
        return id++;
    }

    @Override
    public User updateUser(Long id, User user) {
        User oldUser = usersStorage.get(id);
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(oldUser.getEmail())) {
                for (User userInStorage : usersStorage.values()) {
                    if (user.getEmail().equals(userInStorage.getEmail())) {
                        throw new DuplicationException("Email already exist");
                    }
                }
                oldUser.setEmail(user.getEmail());
            }
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        usersStorage.put(id, oldUser);
        return usersStorage.get(id);
    }

    @Override
    public Optional<User> getById(Long id) {
        if (id == null || !usersStorage.containsKey(id)) {
            throw new NotFoundException();
        }
        return Optional.of(usersStorage.get(id));
    }

    @Override
    public void removeById(Long id) {
        usersStorage.remove(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(usersStorage.values());
    }
}