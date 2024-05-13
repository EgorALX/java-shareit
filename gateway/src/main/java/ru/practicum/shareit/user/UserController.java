package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable long userId) {
        log.info("Get user with id {}", userId);
        return userClient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserCreateDto userDto) {
        log.info("Create user {}", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto,
                                         @PathVariable long userId) {
        log.info("Update user {} with id {}", userUpdateDto, userId);
        return userClient.updateUser(userUpdateDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeById(@PathVariable long userId) {
        log.info("Delete user with id {}", userId);
        return userClient.removeById(userId);
    }
}