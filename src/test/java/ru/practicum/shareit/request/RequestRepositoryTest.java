package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RequestRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void findAllByRequesterIdTest() {
        User user1 = new User("user1", "user1@yandex.ru");
        User user2 = new User("user2", "user2@yandex.ru");
        Item item = new Item("item1", "itemm", true, user1, null);
        Request request2 = new Request("req", user1, LocalDateTime.now());
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user2, Status.APPROVED);
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);

        User requester = userRepository.save(user1);
        Request request = requestRepository.save(request2);
        Sort sort = Sort.by(Sort.Direction.ASC, "created");

        List<Request> requests = requestRepository.findAllByRequesterId(requester.getId(), sort);

        assertThat(requests).isNotEmpty();
        assertThat(requests.get(0).getId()).isEqualTo(request.getId());
        assertThat(requests.get(0).getRequester().getId()).isEqualTo(requester.getId());
    }

    @Test
    void findAllByRequesterIdNotTest() {
        User user1 = new User("user1", "user3@yandex.ru");
        User user2 = new User("user2", "user4@yandex.ru");
        Item item = new Item("item12", "itemmь", true, user1, null);
        Request request2 = new Request("req", user1, LocalDateTime.now());
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user2, Status.APPROVED);

        User requester = userRepository.save(user1);
        User otherUser = userRepository.save(user2);
        Request request = requestRepository.save(request2);
        Pageable pageable = PageRequest.of(0, 10);

        List<Request> requests = requestRepository.findAllByRequesterIdNot(otherUser.getId(), pageable);

        assertThat(requests).isNotEmpty();
        assertThat(requests.get(0).getId()).isEqualTo(request.getId());
        assertThat(requests.get(0).getRequester().getId()).isEqualTo(requester.getId());
    }
}
