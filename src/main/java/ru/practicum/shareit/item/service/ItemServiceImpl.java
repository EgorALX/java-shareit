package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapping.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.model.AccessException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapping.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final ItemMapper itemMapper;

    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemCreateDto itemCreateDto) {
        if (userId == null) {
            throw new NotFoundException("User " + userId + " not found");
        }
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Item newItem = itemMapper.toItem(itemCreateDto, owner);
        return itemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemCreateDto itemDto) {
        if (itemDto.getId() == null) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item " + itemDto.getId() + " not found"));
        User user = userRepository.findById(itemDto.getUserId())
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
        Item neededItem = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Предмет " + id + " не найден"));
        List<ItemDto> itemDtoList = Collections.singletonList(itemMapper.toItemDto(neededItem));

        if (neededItem.getOwner().getId().equals(userId)) {
            Map<Long, BookingForItem> lastBookings = bookingRepository.findFirstByItemIdInAndStartLessThanEqualAndStatus(
                            Collections.singletonList(id), LocalDateTime.now(), Status.APPROVED, Sort.by(DESC, "start"))
                    .stream()
                    .map(bookingMapper::toBookingForItemDto)
                    .collect(Collectors.toMap(BookingForItem::getItemId, Function.identity()));

            Map<Long, BookingForItem> nextBookings = bookingRepository.findFirstByItemIdInAndStartAfterAndStatus(
                            Collections.singletonList(id), LocalDateTime.now(), Status.APPROVED, Sort.by(ASC, "start"))
                    .stream()
                    .map(bookingMapper::toBookingForItemDto)
                    .collect(Collectors.toMap(BookingForItem::getItemId, Function.identity()));
            ItemDto itemDto = itemDtoList.get(0);
            itemDto.setLastBooking(lastBookings.get(id));
            itemDto.setNextBooking(nextBookings.get(id));
        }
        ItemDto item = itemDtoList.get(0);
        item.setComments(commentRepository.getAllByItemId(id).stream().map(commentMapper::toCommentDto)
                .collect(Collectors.toList()));

        return item;
    }

    @Override
    public List<ItemDto> getUsersItems(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<Item> items = itemRepository.findItemByOwnerId(userId);
        List<ItemDto> itemDtoList = items.stream().map(item -> {
            ItemDto itemDto = itemMapper.toItemDto(item);
            Map<Long, BookingForItem> lastBookings = bookingRepository.findFirstByItemIdInAndStartLessThanEqualAndStatus(
                            Collections.singletonList(item.getId()), LocalDateTime.now(), Status.APPROVED, Sort.by(DESC, "start"))
                    .stream()
                    .map(bookingMapper::toBookingForItemDto)
                    .collect(Collectors.toMap(BookingForItem::getItemId, Function.identity()));
            Map<Long, BookingForItem> nextBookings = bookingRepository.findFirstByItemIdInAndStartAfterAndStatus(
                            Collections.singletonList(item.getId()), LocalDateTime.now(), Status.APPROVED, Sort.by(ASC, "start"))
                    .stream()
                    .map(bookingMapper::toBookingForItemDto)
                    .collect(Collectors.toMap(BookingForItem::getItemId, Function.identity()));
            itemDto.setLastBooking(lastBookings.get(item.getId()));
            itemDto.setNextBooking(nextBookings.get(item.getId()));
            itemDto.setComments(commentRepository.getAllByItemId(item.getId()).stream()
                    .map(commentMapper::toCommentDto)
                    .collect(Collectors.toList()));
            return itemDto;
        }).collect(Collectors.toList());

        return itemDtoList;
    }


    @Override
    @Transactional
    public void removeById(Long userId, Long itemId) {
        itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item " + itemId + " not found"));
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        itemRepository.deleteById(itemId);
    }


    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text, text, true)) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }

    @Transactional
    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentCreateDto comment) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User " + userId + " not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item " + itemId + " not found"));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new AccessException("Access error");
        }
        Comment newComment = commentMapper.toComment(comment);
        newComment.setItem(item);
        newComment.setAuthor(user);
        newComment.setCreatedDate(LocalDateTime.now());
        commentRepository.save(newComment);
        return commentMapper.toCommentDto(newComment);
    }
}
