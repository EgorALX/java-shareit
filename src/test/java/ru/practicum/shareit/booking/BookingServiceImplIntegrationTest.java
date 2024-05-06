package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BookingServiceImplIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        entityManager.clear();
    }

    @Test
    void testCreateBooking() {
        User user = new User(2L, "User1", "user1@example.com");
        User user3 = new User(3L, "fksf", "sdfddfds@example.com");
        Item item = new Item(2L, "Item1", "Description1", true, user, null);
        Booking booking = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user, Status.WAITING);

        entityManager.merge(user);
        entityManager.merge(user3);
        entityManager.merge(item);
        entityManager.merge(booking);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());

        BookingDto result = bookingService.create(3L, bookingCreateDto);

        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());
    }


    @Test
    void testCreateBookingWithInvalidItemId() {
        User user = new User(1L, "User1", "user1@example.com");
        Item item = new Item(1L, "Item1", "Description1", true, user, null);
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user, Status.WAITING);

        entityManager.merge(user);
        entityManager.merge(item);
        entityManager.merge(booking);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(999L);
        bookingCreateDto.setStart(booking.getStart());
        bookingCreateDto.setEnd(booking.getEnd());

        assertThrows(NotFoundException.class, () -> bookingService.create(user.getId(), bookingCreateDto));
    }

}
