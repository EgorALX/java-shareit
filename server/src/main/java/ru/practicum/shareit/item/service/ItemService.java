package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemCreateDto item);

    ItemDto updateItem(ItemCreateDto item);

    ItemDto getById(Long userId, Long id);

    void removeById(Long userId, Long id);

    List<ItemDto> getUsersItems(Long userId, Pageable pageable);

    List<ItemDto> search(String text, Pageable pageable);

    CommentDto addComment(Long itemId, Long userId, CommentCreateDto comment);

    List<ItemDto> getItemsByRequestId(Long id);
}
