package ru.practicum.shareit.item;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapping.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {

    @InjectMocks
    private ItemMapper itemMapper;

    @Test
    public void testToItemDto() {
        Item item = new Item(1L, "Book", "A", null, null, new User());

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    public void testToItem() {
        User owner = new User(1L, "password", "email");

        ItemCreateDto itemCreateDto = new ItemCreateDto(1L, "Book", "A",
                true, null, null);

        Item item = itemMapper.toItem(itemCreateDto, owner);

        assertEquals(itemCreateDto.getId(), item.getId());
        assertEquals(itemCreateDto.getName(), item.getName());
        assertEquals(itemCreateDto.getDescription(), item.getDescription());
        assertEquals(itemCreateDto.getAvailable(), item.getAvailable());
        assertEquals(owner, item.getOwner());
    }
}
