package ru.practicum.shareit.booking.mapping;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.model.Booking;

@Component
@NoArgsConstructor
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public Booking toBooking(BookingCreateDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd());
    }

    public BookingForItem bookingForItemDto(Booking booking) {
        if (booking != null) {
            return new BookingForItem(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getItem().getId(),
                    booking.getBooker().getId());
        } else {
            return null;
        }
    }
}
