package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.Stack;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                             @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Creating a new booking for userId: {}", userId);
        BookingDto result = bookingService.create(userId, bookingCreateDto);
        log.info("Booking created successfully for userId: {}", userId);
        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader(USER_ID_HEADER) Long userId,
                             @RequestParam Boolean approved) {
        log.info("Updating booking with id: {} for userId: {}", bookingId, userId);
        BookingDto result = bookingService.update(bookingId, userId, approved);
        log.info("Booking updated successfully with id: {} for userId: {}", bookingId, userId);
        return result;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId,
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Getting booking by id: {} for userId: {}", bookingId, userId);
        BookingDto result = bookingService.getById(bookingId, userId);
        log.info("Booking retrieved successfully by id: {} for userId: {}", bookingId, userId);
        return result;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @RequestParam(defaultValue = "ALL") String state) {
        log.info("Getting bookings by owner for userId: {} with state: {}", userId, state);
        List<BookingDto> result = bookingService.getBookingsByOwner(userId, State.convertStateStringToEnum(state));
        log.info("Bookings retrieved successfully by owner for userId: {} with state: {}", userId, state);
        return result;
    }

    @GetMapping
    public List<BookingDto> getBookingsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        log.info("Getting bookings by user for userId: {} with state: {}", userId, state);
        List<BookingDto> result = bookingService.getBookingsByUser(userId, State.convertStateStringToEnum(state));
        log.info("Bookings retrieved successfully by user for userId: {} with state: {}", userId, state);
        return result;
    }
}
