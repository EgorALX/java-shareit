package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RequestServiceImplIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RequestServiceImpl requestService;

    @Test
    void createRequestTest() {
        User user = new User(1L, "User1", "user1@example.com");
        entityManager.merge(user);
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto(1L, "Request1",
                1L, LocalDateTime.now());
        requestDto.setRequesterId(user.getId());
        ItemRequestDto createdRequest = requestService.create(requestDto);

        assertNotNull(createdRequest, "Запрос должен быть создан");
        assertEquals(requestDto.getDescription(), createdRequest.getDescription(),
                "Описание запроса должно совпадать");
    }

}