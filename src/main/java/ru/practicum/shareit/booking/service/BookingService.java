package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

@Service
public interface BookingService {

    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) throws Exception;

    public BookingDto update(Long bookingId, Long userId, Boolean approved) throws Exception;

    public BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByOwner(Long userId, State state) throws Exception;

    List<BookingDto> getBookingsByUser(Long userId, State state) throws Exception;
}
