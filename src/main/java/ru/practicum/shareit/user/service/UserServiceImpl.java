package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    private final UserMapper mapper;

    @Override
    @Transactional
    public UserDto addUser(UserCreateDto userCreateDto) {
        User addedUser = storage.save(mapper.toUser(userCreateDto));
        return mapper.toUserDto(addedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        User updatedUser = storage.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        if (userUpdateDto.getEmail() != null) {
            updatedUser.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getName() != null) {
            updatedUser.setName(userUpdateDto.getName());
        }
        return mapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getById(Long id) {
        User user = storage.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        return mapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void removeById(Long id) {
        storage.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return storage.findAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }
}