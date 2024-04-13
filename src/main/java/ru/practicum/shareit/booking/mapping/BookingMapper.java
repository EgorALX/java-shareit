package ru.practicum.shareit.booking.mapping;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
@NoArgsConstructor
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public Booking toBooking(BookingCreateDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStartDate(),
                bookingDto.getEndDate());
    }
}
