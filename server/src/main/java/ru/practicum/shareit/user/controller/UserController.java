package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserCreateDto userCreateDto) {
        log.info("Adding user with email:  {}", userCreateDto.getEmail());
        UserDto addedUserDto = userService.addUser(userCreateDto);
        log.info("User added with id: {}", addedUserDto.getId());
        return addedUserDto;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Updating user with id: {}", id);
        UserDto updatedUserDto = userService.updateUser(id, userUpdateDto);
        log.info("User updated with id: {}", updatedUserDto.getId());
        return updatedUserDto;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        UserDto userDto = userService.getById(id);
        log.info("User found with id: {}", userDto.getId());
        return userDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable Long id) {
        log.info("Removing user with id: {}", id);
        userService.removeById(id);
        log.info("User removed with id: {}", id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Getting all users");
        List<UserDto> userDtos = userService.getUsers();
        log.info("Found {} users", userDtos.size());
        return userDtos;
    }
}