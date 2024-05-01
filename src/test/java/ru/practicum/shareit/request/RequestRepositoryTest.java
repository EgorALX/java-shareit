package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShareItApp.class)
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestRepositoryTest {
    @Mock
    private RequestRepository requestRepository;
    private RequestService requestService;

    @Test
    void getByIdWrongId() {
        requestService = new RequestServiceImpl(requestRepository, null, null);

        assertThrows(NullPointerException.class,
                () -> requestService.getById(1L, 999L));
    }
}