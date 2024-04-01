package ru.practicum.shareit.item.mapping;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
public class ItemMapper {

    private final UserMapper userMapper = new UserMapper();

    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable());
    }

    public Item toItem(ItemCreateDto item, User owner) {
        return new Item(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), owner);
    }
}
