package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;
    private final UserMapper mapper;

    @Override
    public UserDto addUser(UserCreateDto user) {
        User addedUser = storage.addUser(mapper.toUser(user));
        return mapper.toUserDto(addedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        userUpdateDto.setId(id);
        User user = storage.getById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        if (userUpdateDto.getName() == null) {
            userUpdateDto.setName(user.getName());
        }
        User updatedUser = storage.updateUser(mapper.toUser(userUpdateDto));
        return mapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getById(Long id) {
        User user = storage.getById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        return mapper.toUserDto(user);
    }

    @Override
    public void removeById(Long id) {
        storage.removeById(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return storage.getUsers().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }
}
