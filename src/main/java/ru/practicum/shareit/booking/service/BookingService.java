package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {

    BookingDto create(Long userId, BookingCreateDto bookingCreateDto);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByOwner(Long userId, State state, Pageable pageable);

    List<BookingDto> getBookingsByUser(Long userId, State state, Pageable pageable);
}
