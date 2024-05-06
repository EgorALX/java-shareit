package ru.practicum.shareit.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        entityManager.clear();
        UserCreateDto userCreateDto = new UserCreateDto(1L, "Test User", "test@example.com");
        userService.addUser(userCreateDto);
    }

    @Test
    void addUserTest() {
        UserCreateDto userCreateDto = new UserCreateDto(2L, "Another User", "another@example.com");
        UserDto result = userService.addUser(userCreateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Another User");
        assertThat(result.getEmail()).isEqualTo("another@example.com");
    }

    @Test
    void updateUserTest() {
        UserCreateDto userCreateDto = new UserCreateDto(3L, "Test User", "test@example.com");
        User user = userMapper.toUser(userCreateDto);
        entityManager.find(User.class, 3L);
        entityManager.merge(user);
        entityManager.flush();

        UserUpdateDto userUpdateDto = new UserUpdateDto(2L, "Updated User", "updated@example.com");

        UserDto updatedUserDto = userService.updateUser(user.getId(), userUpdateDto);

        assertThat(updatedUserDto).isNotNull();
        assertThat(updatedUserDto.getName()).isEqualTo("Updated User");
        assertThat(updatedUserDto.getEmail()).isEqualTo("updated@example.com");

        User updatedUser = entityManager.find(User.class, user.getId());
        assertThat(updatedUser.getName()).isEqualTo("Updated User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }
}