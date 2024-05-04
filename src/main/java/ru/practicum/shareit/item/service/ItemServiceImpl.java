package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapping.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
import java.util.*;
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
                () -> new NotFoundException("Item " + id + " not found"));
        List<ItemDto> itemDtoList = Collections.singletonList(itemMapper.toItemDto(neededItem));
        if (neededItem.getOwner().getId().equals(userId)) {
            BookingForItem lastBooking = null;
            Booking lastBookingObject = bookingRepository
                    .findFirstByItemIdInAndStartLessThanEqualAndStatus(
                            Collections.singletonList(id), LocalDateTime.now(),
                            Status.APPROVED, Sort.by(DESC, "start"))
                    .orElse(null);
            if (lastBookingObject != null) {
                lastBooking = bookingMapper.toBookingForItemDto(lastBookingObject);
            }
            BookingForItem nextBooking = null;
            Booking nextBookingObject = bookingRepository.findFirstByItemIdInAndStartAfterAndStatus(
                            Collections.singletonList(id), LocalDateTime.now(), Status.APPROVED,
                            Sort.by(ASC, "start"))
                    .orElse(null);
            if (nextBookingObject != null) {
                nextBooking = bookingMapper.toBookingForItemDto(nextBookingObject);
            }
            ItemDto itemDto = itemDtoList.get(0);
            itemDto.setLastBooking(lastBooking);
            itemDto.setNextBooking(nextBooking);
        }
        ItemDto item = itemDtoList.get(0);
        item.setComments(commentRepository.getAllByItemId(id).stream().map(commentMapper::toCommentDto)
                .collect(toList()));
        return item;
    }

    @Override
    public List<ItemDto> getUsersItems(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));

        List<Item> items = itemRepository.findItemByOwnerId(userId, pageable);
        List<Booking> bookings = bookingRepository.findAllByItemInAndStatus(items, Status.APPROVED);
        Map<Long, List<Booking>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        List<Comment> allComments = commentRepository.findAllByItemIn(items);
        Map<Long, List<CommentDto>> commentsByItemId = allComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(commentMapper::toCommentDto)
                                .collect(Collectors.toList())
                ));

        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : items) {
            List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList());
            Booking lastBooking = getLastBooking(itemBookings);
            Booking nextBooking = getNextBooking(itemBookings);
            ItemDto itemDto = itemMapper.toItemDto(item);
            if (bookingMapper!= null && lastBooking!= null) {
                itemDto.setLastBooking(bookingMapper.toBookingForItemDto(lastBooking));
            }
            if (nextBooking!= null) {
                itemDto.setNextBooking(bookingMapper.toBookingForItemDto(nextBooking));
            }
            itemDto.setComments(commentsByItemId.getOrDefault(item.getId(), Collections.emptyList()));
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }

    private Booking getLastBooking(List<Booking> itemBookings) {
        return itemBookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) ||
                        (booking.getStart().isEqual(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now())))
                .findFirst()
                .orElse(null);
    }

    private Booking getNextBooking(List<Booking> itemBookings) {
        return itemBookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .reduce((first, second) -> second)
                .orElse(null);
    }


    @Override
    @Transactional
    public void removeById(Long userId, Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(String text, Pageable pageable) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        text = text.toLowerCase();
        List<Item> itemsPage = itemRepository.search(text, text, true, pageable);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemsPage) {
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

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return itemRepository.getItemsByRequestId(requestId, Sort.by(DESC, "id"))
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }
}