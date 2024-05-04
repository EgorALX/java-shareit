package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {

    private final RequestService requestService;

    private final UserService userService;

    private final UserDto user1 = new UserDto(1L, "user1", "user1@yandex.ru");
    private final UserCreateDto userdto1 = new UserCreateDto(1L, "userrr2", "userrrr1@yandex.ru");
    private final UserCreateDto userdto2 = new UserCreateDto(2L, "use2", "user2@yandex.ru");
    private final ItemRequestCreateDto request = new ItemRequestCreateDto(5L, "request", user1,
            LocalDateTime.of(2022, 10, 12, 21, 40, 0), null);

    @Test
    void createRequestTest() {
        UserDto thisUser = userService.addUser(userdto2);
        ItemRequestDto thisRequest = requestService.create(thisUser.getId(), request,
                LocalDateTime.of(2020, 1, 1, 2, 1, 1));

        assertThat(thisRequest.getDescription(), equalTo(request.getDescription()));
    }

    @Test
    void createIdIsIncorrectTest() {
        assertThrows(NotFoundException.class,
                () -> requestService.create(500L, request, LocalDateTime.now()));
    }

    @Test
    void getByOwnerTest() {
        UserDto thisUser = userService.addUser(userdto2);
        ItemRequestDto thisRequest = requestService.create(thisUser.getId(), request,
                LocalDateTime.of(2020, 1, 1, 2, 1, 1));
        List<ItemRequestDto> returnedRequest = requestService.getByOwner(thisUser.getId());

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(thisRequest));
    }

    @Test
    void getByOwnerIdIsIncorrectTest() {
        assertThrows(NotFoundException.class,
                () -> requestService.getByOwner(500L));
    }

    @Test
    void getAllTest() {
        UserDto thisUser = userService.addUser(userdto1);
        UserDto thisAnna = userService.addUser(userdto2);
        ItemRequestDto thisRequest = requestService.create(thisUser.getId(), request,
                LocalDateTime.of(2020, 1, 1, 2, 1, 1));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(DESC, "created"));
        List<ItemRequestDto> returnedRequest = requestService.getRequests(thisAnna.getId(), pageable);

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(thisRequest));
    }

    @Test
    void getAllIdIsIncorrectTest() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(DESC, "created"));

        assertThrows(NotFoundException.class,
                () -> requestService.getRequests(500L, pageable));
    }

    @Test
    void getAllSizeIsNullTest() {
        UserDto user = userService.addUser(userdto1);
        UserDto user2 = userService.addUser(userdto2);
        ItemRequestDto thisRequest = requestService.create(user.getId(), request,
                LocalDateTime.of(2020, 1, 1, 2, 1, 1));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(DESC, "created"));

        List<ItemRequestDto> returnedRequest = requestService.getRequests(user2.getId(), pageable);

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(thisRequest));
    }

    @Test
    void getByIdTest() {
        UserDto user = userService.addUser(userdto1);
        ItemRequestDto thisRequest = requestService.create(user.getId(), request,
                LocalDateTime.of(2020, 1, 1, 2, 1, 1));
        ItemRequestDto returnedRequest = requestService.getById(user.getId(), thisRequest.getId());

        assertEquals(thisRequest.getDescription(), returnedRequest.getDescription());
        assertEquals(thisRequest.getRequester(), returnedRequest.getRequester());
    }

    @Test
    void getByIdIdIsIncorrectTest() {
        assertThrows(NotFoundException.class,
                () -> requestService.getById(500L, 500L));
    }

    @Test
    void getByIdUserIdIsIncorrectTest() {
        assertThrows(NotFoundException.class,
                () -> requestService.getById(500L, 500L));
    }

}
