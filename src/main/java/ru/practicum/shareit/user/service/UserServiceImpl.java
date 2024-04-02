package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    private final UserMapper mapper;

    @Override
    public UserDto addUser(UserCreateDto user) {
        log.info("Adding user with email: {}", user.getEmail());
        if (user.getEmail() == null) {
            throw new ValidationException("Data not valid");
        }
        User addedUser = storage.addUser(mapper.toUser(user));
        log.info("User added with id: {}", addedUser.getId());
        return mapper.toUserDto(addedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto user) {
        log.info("Updating user with id: {}", id);
        if (user == null || id == null) {
            throw new NotFoundException("User or id not found");
        }
        user.setId(id);
        storage.getById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        User updatedUser = storage.updateUser(mapper.toUser(user));
        log.info("User updated with id: {}", updatedUser.getId());
        return mapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getById(Long id) {
        log.info("Getting user by id: {}", id);
        User user = storage.getById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        log.info("User found with id: {}", user.getId());
        return mapper.toUserDto(user);
    }

    @Override
    public void removeById(Long id) {
        log.info("Removing user with id: {}", id);
        storage.getById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        storage.removeById(id);
        log.info("User removed with id: {}", id);
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Getting all users");
        List<UserDto> userDtos = storage.getUsers().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Found {} users", userDtos.size());
        return userDtos;
    }
}
