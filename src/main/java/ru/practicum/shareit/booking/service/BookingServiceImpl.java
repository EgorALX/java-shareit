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
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.model.AccessException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper mapper;

    private static final Sort sort = Sort.by(DESC, "start");

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Item " + bookingCreateDto.getItemId() + " not found"));
        if (item.getAvailable().equals(false)) {
            throw new AccessException("Item " + bookingCreateDto.getItemId() + " not available");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User " + userId + " not found"));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Data not found");
        }
        Booking newBooking = mapper.toBooking(bookingCreateDto);
        newBooking.setItem(item);
        newBooking.setBooker(user);
        newBooking.setStatus(Status.WAITING);
        return mapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Transactional
    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking updatedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking " + bookingId + " not found"));
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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking " + bookingId + " not found"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Invalid request");
        }
        return mapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, State state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwner(user, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                return new ArrayList<>();
        }
        return bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, State state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBooker(user, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                return new ArrayList<>();
        }
        return bookingList.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

}
