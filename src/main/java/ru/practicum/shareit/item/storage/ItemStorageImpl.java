package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Long> usersItems = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item addItem(Item item) {
        Long id = setNewId();
        item.setId(id);
        items.put(id, item);
        usersItems.put(item.getOwner().getId(), item.getId());
        return items.get(id);
    }

    private Long setNewId() {
        return id++;
    }

    @Override
    public Item updateItem(Long userId, Item item) {
        if (usersItems.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(userId) && entry.getValue().equals(item.getId()))) {
            Item newItem = items.get(item.getId());
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
            throw new NotFoundException("User with id:" + userId + " or Item with id:" + item.getId() + " not found");
        }
    }

    @Override
    public Optional<Item> getById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Item with id:" + id + " not found");
        }
        return Optional.of(items.get(id));
    }

    @Override
    public void removeById(Long userId, Long itemId) {
        usersItems.remove(userId, itemId);
        items.remove(itemId);
    }

    @Override
    public List<Item> getUsersItems(Long id) {
        return usersItems.entrySet().stream()
                .filter(entry -> id.equals(entry.getKey()))
                .map(entry -> getById(entry.getValue()).orElseThrow(() -> new NotFoundException("User " + id + " not found")))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(itemDto -> !text.isBlank() &&
                        (itemDto.getName().toLowerCase().contains(text.toLowerCase()) ||
                                itemDto.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        itemDto.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

}
