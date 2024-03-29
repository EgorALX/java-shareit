package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    ItemDto getById(Long userId, Long id);

    void removeById(Long userId, Long id);

    List<ItemDto> getUsersItems(Long userId);

    List<ItemDto> search(String text);
}
