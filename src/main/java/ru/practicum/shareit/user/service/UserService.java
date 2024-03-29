package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto addUser(User user);

    UserDto updateUser(Long id, User user);

    UserDto getById(Long id);

    void removeById(Long id);

    List<UserDto> getUsers();
}
