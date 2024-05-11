package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapping.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapping.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BookingMapper bookingMapper;

    @Test
    void toBookingDtoTest() {
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new Item(), new User(), Status.APPROVED);
        BookingDto expectedDto = new BookingDto(1L, booking.getStart(), booking.getEnd(), new ItemDto(), new UserDto(), Status.APPROVED);

        when(itemMapper.toItemDto(booking.getItem())).thenReturn(new ItemDto());
        when(userMapper.toUserDto(booking.getBooker())).thenReturn(new UserDto());

        BookingDto actualDto = bookingMapper.toBookingDto(booking);

        assertEquals(expectedDto, actualDto);
    }


    @Test
    void toBookingTest() {
        BookingCreateDto bookingDto = new BookingCreateDto(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 1L);

        Booking actualBooking = bookingMapper.toBooking(bookingDto);

        assertEquals(1L, actualBooking.getId());
        assertEquals(bookingDto.getStart(), actualBooking.getStart());
        assertEquals(bookingDto.getEnd(), actualBooking.getEnd());
    }


    @Test
    void toBookingForItemDtoTest() {
        Request request = new Request(1L, "req", new User(), LocalDateTime.now());
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new Item(1L, "11", "11", true, request, new User()),
                new User(1L, "name", "ww@mail.ru"), Status.APPROVED);
        BookingForItem expectedDto = new BookingForItem(1L, booking.getStart(), booking.getEnd(),
                1L, 1L);

        BookingForItem actualDto = bookingMapper.toBookingForItemDto(booking);

        assertEquals(expectedDto, actualDto);
    }


}
