package ru.practicum.shareit.user.mapping;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

@Service
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserCreateDto user) {
        return new User(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto user) {
        return new User(user.getId(), user.getName(), user.getEmail());
    }
}
