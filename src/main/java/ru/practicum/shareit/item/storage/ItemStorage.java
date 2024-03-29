package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    Optional<ItemDto> getById(Long id);

    void removeById(Long userId, Long id);

    List<ItemDto> getUsersItems(Long id);

    List<ItemDto> search(String text);
}
