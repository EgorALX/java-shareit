package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    User user1 = new User(1L, "user1", "user1@yandex.ru");

    User user2 = new User(2L, "user2", "user2@yandex.ru");
    Item item = new Item(1L, "item1", "itemm", true, user1, null);

    Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user2, Status.APPROVED);

    @BeforeEach
    void setUp() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByItemId() {
        TypedQuery<Booking> query = entityManager.createQuery("SELECT b FROM Booking AS b WHERE b.item.id = :id",
                Booking.class);
        query.setParameter("id", booking.getItem().getOwner().getId());

        Booking booking1 = query.getSingleResult();
        assertEquals(booking1.getId(), booking.getId());
        assertEquals(booking1.getStart(), booking.getStart());
        assertEquals(booking1.getEnd(), booking.getEnd());
        assertEquals(booking1.getItem().getId(), booking.getItem().getId());
        assertEquals(booking1.getBooker().getId(), booking.getBooker().getId());
        assertEquals(booking1.getStatus(), booking.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllTest() {
        TypedQuery<Booking> query = entityManager.createQuery("SELECT b FROM Booking AS b WHERE b.item.id in :ids",
                Booking.class);
        List<Long> ids = new ArrayList<>();
        ids.add(booking.getItem().getId());
        query.setParameter("ids", ids);
        Booking bookingFromDb = query.getSingleResult();
        assertEquals(bookingFromDb.getId(), booking.getId());
        assertEquals(bookingFromDb.getStart(), booking.getStart());
        assertEquals(bookingFromDb.getEnd(), booking.getEnd());
        assertEquals(bookingFromDb.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingFromDb.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingFromDb.getStatus(), booking.getStatus());
    }
}
