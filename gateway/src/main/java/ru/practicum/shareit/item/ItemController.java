package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Slf4j
@RequestMapping(value = "/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";


    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable long itemId,
                                          @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get item with id {} from user with id {}", itemId, userId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersItems(@RequestHeader(USER_ID_HEADER) long userId,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Get list of items owned by user with с id {}," +
                " beginning from {}, by {} items on page", userId, from, size);
        return itemClient.getUsersItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                         @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Search items by text {}," +
                " beginning from {}, by {} items on page", text, from, size);
        return itemClient.search(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto itemDto,
                                          @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Create item {} by user with id {}", itemDto, userId);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId) {
        log.info("Update item {} by user with id {}", itemDto, userId);
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentCreateDto,
                                             @RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId) {
        log.info("Comment to item with id {} by user with id {}", itemId, userId);
        return itemClient.addComment(commentCreateDto, userId, itemId);
    }
}