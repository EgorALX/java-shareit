package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserStorage storage;

    private final UserMapper mapper;

    @Override
    public UserDto addUser(User user) {
        if (user == null || user.getEmail() == null) {
            throw new ValidationException("Data not valid");
        }
        return mapper.toUserDto(storage.addUser(user));
    }

    @Override
    public UserDto updateUser(Long id, User user) {
        if (user == null || id == null) {
            throw new NotFoundException();
        }
        storage.getById(id).orElseThrow(NotFoundException::new);
        return mapper.toUserDto(storage.updateUser(id, user));
    }

    @Override
    public UserDto getById(Long id) {
        return mapper.toUserDto(storage.getById(id).orElseThrow(NotFoundException::new));
    }

    @Override
    public void removeById(Long id) {
        storage.getById(id).orElseThrow(NotFoundException::new);
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
