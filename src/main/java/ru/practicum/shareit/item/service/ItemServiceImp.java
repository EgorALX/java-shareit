package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(Long userId, ItemDto item) {
        userStorage.getById(userId).orElseThrow(NotFoundException::new);
        return itemStorage.addItem(userId, item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        itemStorage.getById(itemId).orElseThrow(NotFoundException::new);
        userStorage.getById(userId).orElseThrow(NotFoundException::new);
        return itemStorage.updateItem(userId, itemId, item);
    }

    @Override
    public ItemDto getById(Long userId, Long id) {
        return itemStorage.getById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public void removeById(Long userId, Long itemId) {
        itemStorage.getById(itemId).orElseThrow(NotFoundException::new);
        userStorage.getById(userId).orElseThrow(NotFoundException::new);
        itemStorage.removeById(userId, itemId);
    }

    @Override
    public List<ItemDto> getUsersItems(Long userId) {
        userStorage.getById(userId).orElseThrow(NotFoundException::new);
        return itemStorage.getUsersItems(userId);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text);
    }
}
