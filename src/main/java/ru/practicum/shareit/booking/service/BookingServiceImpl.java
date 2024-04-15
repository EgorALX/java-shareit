package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.mapping.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.exception.model.AccessException;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;

    private final UserStorage userStorage;

    private final ItemStorage itemStorage;

    private final BookingMapper mapper;

    private static final Sort sort = Sort.by(DESC, "start");

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        Item item = itemStorage.findById(bookingCreateDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Item " + bookingCreateDto.getItemId() + " not found"));
        if (item.getAvailable().equals(false)) {
            throw new AccessException("Item " + bookingCreateDto.getItemId() + " not available");
        }
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("User " + userId + " not found"));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Data not found");
        }
        if (bookingCreateDto.getStart().equals(bookingCreateDto.getEnd())
                || bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())) {
            throw new ValidationException("Time validation error");
        }
        Booking newBooking = mapper.toBooking(bookingCreateDto);
        newBooking.setItem(item);
        newBooking.setBooker(user);
        newBooking.setStatus(Status.WAITING);
        return mapper.toBookingDto(bookingStorage.save(newBooking));
    }

    @Transactional
    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking updatedBooking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking " + bookingId + " not found"));
        if (!userId.equals(updatedBooking.getItem().getOwner().getId())) {
            throw new NotFoundException("Data not found");
        }
        if (!updatedBooking.getStatus().equals(Status.WAITING)) {
            throw new AccessException("Unsuitable status");
        }
        if (approved) {
            updatedBooking.setStatus(Status.APPROVED);
        } else {
            updatedBooking.setStatus(Status.REJECTED);
        }
        return mapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking " + bookingId + " not found"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Invalid request");
        }
        return mapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, State state) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("User " + userId + " not found"));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingStorage.findAllByItemOwner(user, sort);
                break;
            case CURRENT:
                bookingList = bookingStorage.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                bookingList = bookingStorage.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingList = bookingStorage.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingList = bookingStorage.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingStorage.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new AccessException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, State state) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingStorage.findAllByBooker(user, sort);
                break;
            case CURRENT:
                bookingList = bookingStorage.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                bookingList = bookingStorage.findAllByBookerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingList = bookingStorage.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingList = bookingStorage.findAllByBookerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingStorage.findAllByBookerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new AccessException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList());
    }
}
