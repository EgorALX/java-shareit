package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    private final UserMapper mapper;

    @Override
    public UserDto addUser(UserCreateDto user) {
        if (user == null || user.getEmail() == null) {
            throw new ValidationException("Data not valid");
        }
        return mapper.toUserDto(storage.addUser(mapper.toUser(user)));
    }

    @Override
    public UserDto updateUser(Long id, UserCreateDto user) {
        if (user == null || id == null) {
            throw new NotFoundException("User or id not found");
        }
        user.setId(id);
        storage.getById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        return mapper.toUserDto(storage.updateUser(mapper.toUser(user)));
    }

    @Override
    public UserDto getById(Long id) {
        return mapper.toUserDto(storage.getById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found")));
    }

    @Override
    public void removeById(Long id) {
        storage.getById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        storage.removeById(id);
    }

    @Override
    public List<UserDto> getUsers() {
        ArrayList<UserDto> list = new ArrayList<>();
        for (User user : storage.getUsers()) {
            list.add(mapper.toUserDto(user));
        }
        return list;
    }
}
