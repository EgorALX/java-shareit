package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.DuplicationException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl {

    private Long id = 1L;
    private final Map<Long, User> usersStorage = new HashMap<>();

    private final Set<String> emailsStorage = new HashSet<>();

    public User addUser(User user) {
        if (!emailsStorage.add(user.getEmail())) {
            throw new DuplicationException("Email already exist");
        }
        Long id = setNewId();
        user.setId(id);
        usersStorage.put(id, user);
        return usersStorage.get(id);
    }

    private Long setNewId() {
        return id++;
    }

    public User updateUser(User user) {
        User oldUser = usersStorage.get(user.getId());
        if (user.getEmail() != null && !user.getEmail().equals(oldUser.getEmail())) {
            if (!emailsStorage.add(user.getEmail())) {
                throw new DuplicationException("Email already exist");
            }
            emailsStorage.remove(oldUser.getEmail());
            oldUser.setEmail(user.getEmail());
        }
        oldUser.setName(user.getName());
        usersStorage.put(user.getId(), oldUser);
        return oldUser;
    }

    public Optional<User> getById(Long id) {
        if (id == null || !usersStorage.containsKey(id)) {
            throw new NotFoundException("User " + id + " not found");
        }
        return Optional.of(usersStorage.get(id));
    }

    public void removeById(Long id) {
        User user = usersStorage.get(id);
        emailsStorage.remove(user.getEmail());
        usersStorage.remove(id);
    }

    public List<User> getUsers() {
        return new ArrayList<>(usersStorage.values());
    }
}