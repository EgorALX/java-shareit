package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemCreateDto item);

    ItemDto updateItem(ItemCreateDto item);

    ItemDto getById(Long userId, Long id);

    void removeById(Long userId, Long id);

    List<ItemDto> getUsersItems(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto addComment(Long itemId, Long userId, CommentCreateDto comment);

    List<ItemDto> getItemsByRequestId(Long id);
}
