package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemCreateDto item);

    ItemDto updateItem(ItemCreateDto item);

    ItemDto getById(Long id);

    void removeById(Long userId, Long id);

    List<ItemDto> getUsersItems(Long userId);

    List<ItemDto> search(String text);

}
