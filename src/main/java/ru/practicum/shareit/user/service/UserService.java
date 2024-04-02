package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserCreateDto user);

    UserDto updateUser(Long id, UserUpdateDto user);

    UserDto getById(Long id);

    void removeById(Long id);

    List<UserDto> getUsers();
}
