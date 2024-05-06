package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testAddItem() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        ItemDto result = itemService.addItem(1L, itemCreateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void testUpdateItem() {
        User user = new User();
        user.setId(2L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);
        ItemDto itemDto = itemService.addItem(user.getId(), itemCreateDto);

        itemCreateDto.setName("Updated Item");
        itemCreateDto.setDescription("Updated Description");
        itemCreateDto.setAvailable(false);
        itemCreateDto.setId(2L);
        itemCreateDto.setUserId(2L);
        ItemDto updatedResult = itemService.updateItem(itemCreateDto);

        assertThat(updatedResult).isNotNull();
        assertThat(updatedResult.getName()).isEqualTo("Updated Item");
        assertThat(updatedResult.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedResult.getAvailable()).isFalse();
    }

}
