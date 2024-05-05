package ru.practicum.shareit.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.exception.model.DuplicationException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest
public class UserRepositoryTest {
    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private final UserMapper mapper = new UserMapper();

    private final UserCreateDto userCreated = new UserCreateDto(1L, "Egor", "egor@yandex.ru");

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, mapper);
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(mapper.toUser(userCreated)));

        UserDto user = userService.getById(1L);
        verify(userRepository, Mockito.times(1)).findById(1L);
        assertThat(userCreated.getName(), equalTo(user.getName()));
        assertThat(userCreated.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getByIdThrowExceptionNotFoundTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class, () -> userService.getById(999L));
        assertEquals("User " + 999L + " not found", exception.getMessage());
    }

    @Test
    void throwExceptionDuplicationTest() {
        when(userRepository.save(any()))
                .thenThrow(new DuplicationException("User already exist"));

        final DuplicationException exception = assertThrows(
                DuplicationException.class, () -> userService.addUser(userCreated));
        assertEquals("User already exist", exception.getMessage());
    }
}