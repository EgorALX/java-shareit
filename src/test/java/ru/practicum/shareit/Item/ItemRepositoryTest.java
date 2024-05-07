package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    RequestRepository requestRepository;

    User user1 = new User(1L, "user1", "user1@mail.ru");

    @Test
    void findItemByOwnerIdTest() {
        User user2 = new User("user5", "user5@mail.ru");
        User user = userRepository.save(user2);
        Item item1 = new Item(1L, "item1", "itemm", true, user, null);
        User owner = userRepository.save(user);
        Item item = itemRepository.save(item1);
        Pageable pageable = PageRequest.of(0, 10);

        List<Item> items = itemRepository.findItemByOwnerId(owner.getId(), pageable);

        assertThat(items).isNotNull();
        assertThat(items.get(0).getId()).isEqualTo(item.getId());
        assertThat(items.get(0).getName()).isEqualTo(item.getName());
        assertThat(items.get(0).getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    void searchTest() {
        User user1 = new User(1L, "user1", "user1@mail.ru");
        Item item1 = new Item(1L, "item1", "itemm", true, user1, null);
        User user = userRepository.save(user1);
        Item item = itemRepository.save(item1);
        Pageable pageable = PageRequest.of(0, 10);

        List<Item> items = itemRepository.search("item", "description item 1", true, pageable);

        assertThat(items).isNotNull();
        assertThat(items.get(0).getId()).isEqualTo(item.getId());
        assertThat(items.get(0).getName()).isEqualTo(item.getName());
        assertThat(items.get(0).getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    void getItemsByRequestIdTest() {
        User user2 = new User("user2", "user1@mail.ru");
        User notOwner = userRepository.save(user2);
        User user3 = new User("user3", "user3@mail.ru");
        User owner = userRepository.save(user3);
        Item item2 = new Item("item2", "itemmь", true, owner, null);
        Item item = itemRepository.save(item2);
        Request request2 = new Request(1L, "req", user2, LocalDateTime.now());
        Request request = requestRepository.save(request2);

        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        List<Item> items = itemRepository.getItemsByRequestId(request.getId(), sort);

        assertThat(items).isNotNull();
    }
}
