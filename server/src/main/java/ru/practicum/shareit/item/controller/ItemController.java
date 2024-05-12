package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader(USER_ID_HEADER) long userId, @Valid @RequestBody ItemCreateDto item) {
        log.info("Adding item for user with id: {}", userId);
        ItemDto addedItemDto = itemService.addItem(userId, item);
        log.info("Item added with id: {}", addedItemDto.getId());
        return addedItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemCreateDto item) {
        log.info("Updating item with id: {}", itemId);
        item.setId(itemId);
        item.setUserId(userId);
        ItemDto updatedItemDto = itemService.updateItem(item);
        log.info("Item updated with id: {}", updatedItemDto.getId());
        return updatedItemDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId) {
        log.info("Getting item by id: {}", itemId);
        ItemDto itemDto = itemService.getById(userId, itemId);
        log.info("Item found with id: {}", itemDto.getId());
        return itemDto;
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable Long itemId) {
        log.info("Removing item with id: {}", itemId);
        itemService.removeById(userId, itemId);
        log.info("Item removed with id: {}", itemId);
    }

    @GetMapping
    public List<ItemDto> getUsersItems(@RequestHeader(USER_ID_HEADER) long userId,
                                       @RequestParam(defaultValue = "0") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting items for user with id: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        List<ItemDto> itemDtos = itemService.getUsersItems(userId, pageable);
        log.info("Found {} items for user with id: {}", itemDtos.size(), userId);
        return itemDtos;
    }


    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") Integer page,
                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Searching items with text: {}", text);
        Pageable pageable = PageRequest.of(page, size);
        List<ItemDto> itemDtos = itemService.search(text, pageable);
        log.info("Found {} items matching the search text", itemDtos.size());
        return itemDtos;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                 @Valid @RequestBody CommentCreateDto comment,
                                 @PathVariable Long itemId) {
        return itemService.addComment(itemId, userId, comment);
    }
}
