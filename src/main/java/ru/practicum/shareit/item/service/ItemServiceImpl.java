package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapping.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto addItem(Long userId, ItemCreateDto itemCreateDto) {
        if (userId == null) {
            throw new NotFoundException("user id not found");
        }
        User owner = userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Item item = itemMapper.toItem(itemCreateDto, owner);
        item.setOwner(owner);
        Item addedItem = itemStorage.addItem(item);
        return itemMapper.toItemDto(addedItem);
    }

    @Override
    public ItemDto updateItem(ItemCreateDto itemDto) {
        if (itemDto.getId() == null) {
            throw new NotFoundException("Data not found");
        }
        Item item = itemStorage.getById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item " + itemDto.getId() + " not found"));
        User user = userStorage.getById(itemDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User " + itemDto.getUserId() + " not found"));
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new NotFoundException("User with id:" + user.getId()
                    + " is not the owner of Item with id:" + item.getId());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemStorage.updateItem(item);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Item " + id + " not found"));
        return itemMapper.toItemDto(item);
    }

    @Override
    public void removeById(Long userId, Long itemId) {
        itemStorage.getById(itemId).orElseThrow(() -> new NotFoundException("Item " + itemId + " not found"));
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        itemStorage.removeById(itemId);
    }

    @Override
    public List<ItemDto> getUsersItems(Long userId) {
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<ItemDto> itemDtos = itemStorage.getUsersItems(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        return itemDtos;
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> itemDtos = itemStorage.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        return itemDtos;
    }
}
