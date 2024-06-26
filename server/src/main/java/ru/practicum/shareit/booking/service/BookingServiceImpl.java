package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

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
        Booking newBooking = bookingMapper.toBooking(bookingCreateDto);
        newBooking.setItem(item);
        newBooking.setBooker(user);
        newBooking.setStatus(Status.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(newBooking));
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
        return bookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking " + bookingId + " not found"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Invalid request");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, State state, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<Booking> bookingPage = Collections.emptyList();
        switch (state) {
            case ALL:
                bookingPage = bookingRepository.findAllByItemOwner(user, pageable);
                break;
            case CURRENT:
                bookingPage = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookingPage = bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingPage = bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingPage = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookingPage = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, pageable);
                break;
        }
        return bookingPage.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, State state, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User " + userId + " not found"));
        List<Booking> bookingPage = getPages(state, user, pageable);
        return bookingPage.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    private List<Booking> getPages(State state, User user, Pageable pageable) {
        List<Booking> bookingPage = Collections.emptyList();
        switch (state) {
            case ALL:
                bookingPage = bookingRepository.findAllByBooker(user, pageable);
                break;
            case CURRENT:
                bookingPage = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookingPage = bookingRepository.findAllByBookerAndEndBefore(user,
                        LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingPage = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingPage = bookingRepository.findAllByBookerAndStatusEquals(user, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookingPage = bookingRepository.findAllByBookerAndStatusEquals(user, Status.REJECTED, pageable);
                break;
        }
        return bookingPage;
    }

}