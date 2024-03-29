package ru.practicum.shareit.item.mapping;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Service
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner());
    }

    public Item toItem(ItemDto item) {
        return new Item(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner());
    }
}
