package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> usersItems = new HashMap<>();
    private Long id = 1L;

    public Item addItem(Item item) {
        Long id = setNewId();
        item.setId(id);
        items.put(id, item);
        usersItems.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>()).add(item);
        return items.get(id);
    }

    private Long setNewId() {
        return id++;
    }

    public Item updateItem(Item item) {
        return items.put(item.getId(), item);
    }

    public Optional<Item> getById(Long id) {
        return Optional.of(items.get(id));
    }

    public void removeById(Long itemId) {
        Long userId = items.get(itemId).getOwner().getId();
        usersItems.get(userId).remove(itemId);
        items.remove(itemId);
    }

    public List<Item> getUsersItems(Long id) {
        return usersItems.getOrDefault(id, new ArrayList<>());
    }

    public List<Item> search(String text) {
        return items.values().stream()
                .filter(itemDto -> !text.isBlank() &&
                        (itemDto.getName().toLowerCase().contains(text.toLowerCase()) ||
                                itemDto.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        itemDto.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

}
