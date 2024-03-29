package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImp implements ItemStorage {

    private final Map<Long, ItemDto> items = new HashMap<>();
    private final Map<Long, Long> usersItems = new HashMap<>();
    private Long id = 1L;

    @Override
    public ItemDto addItem(Long userId, ItemDto item) {
        Long id = setNewId();
        item.setId(id);
        items.put(id, item);
        usersItems.put(userId, item.getId());
        return items.get(id);
    }

    private Long setNewId() {
        return id++;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        if (usersItems.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(userId) && entry.getValue().equals(itemId))) {
            ItemDto newItem = items.get(itemId);
            if (item.getName() != null) {
                newItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                newItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                newItem.setAvailable(item.getAvailable());
            }
            return items.put(newItem.getId(), newItem);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public Optional<ItemDto> getById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException();
        }
        return Optional.of(items.get(id));
    }

    @Override
    public void removeById(Long userId, Long itemId) {
        usersItems.remove(userId, itemId);
        items.remove(itemId);
    }

    @Override
    public List<ItemDto> getUsersItems(Long id) {
        return usersItems.entrySet().stream()
                .filter(entry -> id.equals(entry.getKey()))
                .map(entry -> getById(entry.getValue()).orElseThrow(NotFoundException::new))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return items.values().stream()
                .filter(itemDto -> !text.isBlank() &&
                        (itemDto.getName().toLowerCase().contains(text.toLowerCase()) ||
                                itemDto.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        itemDto.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

}
