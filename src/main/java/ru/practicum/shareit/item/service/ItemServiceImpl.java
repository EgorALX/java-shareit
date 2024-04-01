package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapping.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    @Override
    public ItemDto addItem(Long userId, ItemCreateDto itemCreateDto) {
        User owner = userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Item item = itemMapper.toItem(itemCreateDto, itemCreateDto.getOwner());
        item.setOwner(owner);
        return itemMapper.toItemDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto updateItem(ItemCreateDto item) {
        if (item == null || item.getId() == null) {
            throw new NotFoundException("Data not found");
        }
        itemStorage.getById(item.getId()).orElseThrow(() -> new NotFoundException("Item " + item.getId() + " not found"));
        User user = userStorage.getById(item.getUserId()).orElseThrow(() -> new NotFoundException("User " + item.getUserId() + " not found"));
        return itemMapper.toItemDto(itemStorage.updateItem(item.getUserId(), itemMapper.toItem(item, user)));
    }

    @Override
    public ItemDto getById(Long id) {
        return itemMapper.toItemDto(itemStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Item " + id + " not found")));
    }

    @Override
    public void removeById(Long userId, Long itemId) {
        itemStorage.getById(itemId).orElseThrow(() -> new NotFoundException("Item " + itemId + " not found"));
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        itemStorage.removeById(userId, itemId);
    }

    @Override
    public List<ItemDto> getUsersItems(Long userId) {
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        ArrayList<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemStorage.getUsersItems(userId)) {
            itemDtos.add(itemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> search(String text) {
        ArrayList<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemStorage.search(text)) {
            itemDtos.add(itemMapper.toItemDto(item));
        }
        return itemDtos;
    }
}
