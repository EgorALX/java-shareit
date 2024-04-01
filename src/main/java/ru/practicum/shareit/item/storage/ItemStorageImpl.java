package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> usersItems = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item addItem(Item item) {
        Long id = setNewId();
        item.setId(id);
        items.put(id, item);
        usersItems.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>()).add(item.getId());
        return items.get(id);
    }

    private Long setNewId() {
        return id++;
    }

    @Override
    public Item updateItem(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> getById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Item with id:" + id + " not found");
        }
        return Optional.of(items.get(id));
    }

    @Override
    public void removeById(Long itemId) {
        Long userId = items.get(itemId).getOwner().getId();
        usersItems.get(userId).remove(itemId);
        items.remove(itemId);
    }

    @Override
    public List<Item> getUsersItems(Long id) {
        return usersItems.getOrDefault(id, Collections.emptyList()).stream()
                .map(itemId -> getById(itemId).orElseThrow(() -> new NotFoundException("Item with id:" + itemId + " not found")))
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
