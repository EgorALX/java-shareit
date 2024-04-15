package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapping.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.exception.model.AccessException;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

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

    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemCreateDto itemCreateDto) {
        if (userId == null) {
            throw new NotFoundException("User " + userId + " not found");
        }
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Item newItem = itemMapper.toItem(itemCreateDto, owner);
        return itemMapper.toItemDto(itemStorage.save(newItem));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemCreateDto itemDto) {
        if (itemDto.getId() == null) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemStorage.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item " + itemDto.getId() + " not found"));
        User user = userStorage.findById(itemDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User " + itemDto.getUserId() + " not found"));
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new NotFoundException("User with id: " + user.getId()
                    + " is not the owner of Item with id: " + item.getId());
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
    public ItemDto getById(Long userId, Long id) {
        Item neededItem = itemStorage.findById(id).orElseThrow(
                () -> new NotFoundException("Item " + id + " not found"));
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemDtoList.add(itemMapper.toItemDto(neededItem));
        if (neededItem.getOwner().getId().equals(userId)) {
            List<Long> items = itemDtoList.stream().map(ItemDto::getId).collect(toList());
            Map<Long, BookingForItem> lastBookings = bookingStorage.findFirstByItemIdInAndStartLessThanEqualAndStatus(
                            items, LocalDateTime.now(), Status.APPROVED, Sort.by(DESC, "start"))
                    .stream()
                    .map(bookingMapper::bookingForItemDto)
                    .collect(Collectors.toMap(BookingForItem::getItemId, Function.identity()));
            Map<Long, BookingForItem> nextBookings = bookingStorage.findFirstByItemIdInAndStartAfterAndStatus(
                            items, LocalDateTime.now(), Status.APPROVED, Sort.by(ASC, "start"))
                    .stream()
                    .map(bookingMapper::bookingForItemDto)
                    .collect(Collectors.toMap(BookingForItem::getItemId, Function.identity()));
            for (ItemDto item: itemDtoList) {
                item.setLastBooking(lastBookings.get(item.getId()));
                item.setNextBooking(nextBookings.get(item.getId()));
            }
        }
        ItemDto item = itemDtoList.get(0);
        item.setComments(commentStorage.getAllByItemId(id).stream().map(commentMapper::toCommentDto)
                .collect(toList()));
        return item;
    }

    @Override
    public List<ItemDto> getUsersItems(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<Item> items = itemStorage.findItemByOwnerId(userId);
        List<ItemDto> itemDtoList = items.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
        List<Long> itemsIds = itemDtoList.stream().map(ItemDto::getId).collect(Collectors.toList());
        Map<Long, BookingForItem> lastBookings = bookingStorage.findFirstByItemIdInAndStartLessThanEqualAndStatus(
                        itemsIds, LocalDateTime.now(), Status.APPROVED, Sort.by(DESC, "start"))
                .stream()
                .map(bookingMapper::bookingForItemDto)
                .collect(Collectors.toMap(BookingForItem::getItemId, Function.identity()));
        Map<Long, BookingForItem> nextBookings = bookingStorage.findFirstByItemIdInAndStartAfterAndStatus(
                        itemsIds, LocalDateTime.now(), Status.APPROVED, Sort.by(ASC, "start"))
                .stream()
                .map(bookingMapper::bookingForItemDto)
                .collect(Collectors.toMap(BookingForItem::getItemId, Function.identity()));
        for (ItemDto item : itemDtoList) {
            item.setLastBooking(lastBookings.get(item.getId()));
            item.setNextBooking(nextBookings.get(item.getId()));
        }
        return itemDtoList;
    }

    @Override
    @Transactional
    public void removeById(Long userId, Long itemId) {
        itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item " + itemId + " not found"));
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        itemStorage.deleteById(itemId);
    }



    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemStorage
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text, text, true)) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }

    @Transactional
    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentCreateDto comment) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("User " + userId + " not found"));
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item " + itemId + " not found"));
        if (bookingStorage.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty()) {
        throw new AccessException("Access error");
        }
        Comment newComment = commentMapper.toComment(comment);
        newComment.setItem(item);
        newComment.setAuthor(user);
        newComment.setCreatedDate(LocalDateTime.now());
        commentStorage.save(newComment);
        return commentMapper.toCommentDto(newComment);
    }
}
