package ru.practicum.shareit.booking.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapping.ItemMapper;
import ru.practicum.shareit.user.mapping.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemMapper.toItemDto(booking.getItem()),
                userMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }

    public Booking toBooking(BookingCreateDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd());
    }

    public BookingForItem toBookingForItemDto(Booking booking) {
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
