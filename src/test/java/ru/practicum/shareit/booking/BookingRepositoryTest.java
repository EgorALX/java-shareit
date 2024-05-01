package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShareItApp.class)
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {

    private final BookingService bookingService;

    @Test
    void getById_shouldThrowExceptionIfWrongId() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getById(999L, 1L));
    }
}
