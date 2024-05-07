package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void testFindAllByBookerWithPageable() {
        User booker1 = new User("user1", "user1@mail.ru");
        User user = userRepository.save(booker1);
        Item item = new Item("item1", "itemm", true, user, null);
        Item item1 = itemRepository.save(item);
        Item item2 = new Item("item22", "itemm2", true, user, null);
        Item item22 = itemRepository.save(item2);
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item1, user, Status.APPROVED);
        bookingRepository.save(booking);
        Booking booking2 = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item22, booker1, Status.APPROVED);
        bookingRepository.save(booking2);
        Pageable pageable = PageRequest.of(0, 1, Sort.by("start"));

        List<Booking> page = bookingRepository.findAllByBooker(booker1, pageable);

        assertThat(page.size()).isEqualTo(1);
    }


    @Test
    public void testFindAllByItemOwnerAndStatusEqualsWithPageable() {
        User booker11 = new User("user1", "user1@mail.ru");
        User user1 = userRepository.save(booker11);
        Item item1 = new Item("item1", "itemm", true, user1, null);
        Item item11 = itemRepository.save(item1);
        Item item21 = new Item("item22", "itemm2", true, user1, null);
        Item item221 = itemRepository.save(item21);
        Booking booking1 = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item1, user1, Status.APPROVED);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item11, booker11, Status.APPROVED);
        bookingRepository.save(booking2);

        Pageable pageable = PageRequest.of(0, 1, Sort.by("start"));

        List<Booking> page = bookingRepository.findAllByItemOwnerAndStatusEquals(booker11, Status.APPROVED, pageable);

        assertThat(page.size()).isEqualTo(1);
    }

}
