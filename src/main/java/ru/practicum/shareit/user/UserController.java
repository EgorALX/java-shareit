package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        return userService.addUser(userCreateDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateDto userCreateDto) {
        return userService.updateUser(id, userCreateDto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void removeById(@PathVariable Long id) {
        userService.removeById(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }
}
