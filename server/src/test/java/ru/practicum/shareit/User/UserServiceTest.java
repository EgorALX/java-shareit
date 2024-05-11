package ru.practicum.shareit.User;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;

    private final UserCreateDto userCreate = new UserCreateDto(1L, "NeEgor", "neegor@yandex.ru");

    @Test
    void updateUserTest() {
        UserDto user = userService.addUser(userCreate);
        UserUpdateDto userUpdateDto = new UserUpdateDto(user.getId(), "EgorEgor", null);
        UserDto updatedUser = userService.updateUser(user.getId(), userUpdateDto);
        assertEquals(updatedUser.getEmail(), user.getEmail());
    }

    @Test
    void updateNameIsNullTest() {
        UserDto user = userService.addUser(userCreate);
        user.setName(null);
        user.setEmail("neegor@yandex.ru");
        UserUpdateDto userUpdateDto = new UserUpdateDto(user.getId(), null, "neegor@yandex.ru");
        UserDto updatedUser = userService.updateUser(user.getId(), userUpdateDto);
        assertEquals(updatedUser.getEmail(), user.getEmail());
    }

    @Test
    void updateEmailIsNullTest() {
        UserDto user = userService.addUser(userCreate);
        user.setName("NEneEgor");
        user.setEmail(null);
        UserUpdateDto userUpdateDto = new UserUpdateDto(user.getId(), "NEneEgor", null);
        UserDto updatedUser = userService.updateUser(user.getId(), userUpdateDto);
        assertEquals(updatedUser.getName(), user.getName());
    }

    @Test
    void deleteByIdTest() {
        UserDto user = userService.addUser(userCreate);
        userService.removeById(user.getId());
        assertTrue(userService.getUsers().isEmpty());
    }

    @Test
    void getUsersTest() {
        UserDto first = userService.addUser(new UserCreateDto(3L, "EgorEg", "eg@yandex.ru"));
        UserDto second = userService.addUser(new UserCreateDto(5L, "NeEg", "neeg@yandex.ru"));
        assertEquals(2, userService.getUsers().size());
        assertTrue(userService.getUsers().contains(first));
        assertTrue(userService.getUsers().contains(second));
    }
}
