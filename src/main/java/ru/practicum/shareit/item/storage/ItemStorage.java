package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item addItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getById(Long id);

    void removeById(Long id);

    List<Item> getUsersItems(Long id);

    List<Item> search(String text);
}
