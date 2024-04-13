package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapping.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    private final BookingStorage bookingStorage;

    private final CommentStorage commentStorage;

    private final CommentMapper commentMapper;

    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemCreateDto itemCreateDto) {
        if (userId == null) {
            throw new NotFoundException("User id not found");
        }
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Item thisItem = itemMapper.toItem(itemCreateDto, owner);
        return itemMapper.toItemDto(itemStorage.save(thisItem));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemCreateDto itemDto) {
        if (itemDto.getId() == null) {
            throw new NotFoundException("Data not found");
        }
        Item item = itemStorage.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item " + itemDto.getId() + " not found"));
        User user = userStorage.findById(itemDto.getUserId())
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
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Item " + id + " not found"));
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public void removeById(Long userId, Long itemId) {
        itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item " + itemId + " not found"));
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        itemStorage.deleteById(itemId);
    }

    @Override
    public List<ItemDto> getUsersItems(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<Item> itemDtoList = itemStorage.findItemByOwnerId(userId);
        return itemDtoList.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemStorage.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text, text, true)) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentCreateDto comment) throws Exception {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Товар не найден"));
        if (bookingStorage.findAllByBookerIdAndItemIdAndStatusEqualsAndEndDateIsBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new Exception("Ошибка доступа");
        }
        Comment thisComment = commentMapper.toComment(comment);
        thisComment.setItem(item);
        thisComment.setAuthor(user);
        thisComment.setCreatedDate(LocalDateTime.now());
        commentStorage.save(thisComment);
        return commentMapper.toCommentDto(thisComment);
    }
}
