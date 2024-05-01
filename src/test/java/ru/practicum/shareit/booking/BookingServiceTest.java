package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapping.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.model.AccessException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User user1 = new User(1L, "User1", "user1@example.com");
    private final User user2 = new User(2L, "User2", "user2@example.com");
    private final Item item1 = new Item(1L, "Item1", "Description1", true, user1, null);
    private final Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item1, user1, Status.WAITING);

    @BeforeEach
    void setUp() {
        bookingMapper.toBookingDto(booking);
    }

    @Test
    void usersIdIsIncorrect() {
        when(userRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> bookingService.create(999L,
                        new BookingCreateDto(999L, booking.getStart(), booking.getEnd(), booking.getItem().getId())));
    }

    @Test
    void itemsIdIsIncorrect() {
        BookingCreateDto booking = new BookingCreateDto(1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                999L);

        assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(), booking));
    }

    @Test
    void itemsAvailableIncorrect() {
        BookingCreateDto booking = new BookingCreateDto(1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                999L);

        when(itemRepository.findById(any())).thenReturn
                (Optional.of(new Item(1L, null, null, false, null, new User())));

        assertThrows(AccessException.class,
                () -> bookingService.create(user1.getId(), booking));
    }

    @Test
    void itemThrowNotFound() {
        BookingCreateDto booking = new BookingCreateDto(1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                999L);

        when(itemRepository.findById(any())).thenReturn
                (Optional.of(new Item(1L, null, null, true, null, new User())));

        assertThrows(NotFoundException.class,
                () -> bookingService.create(1000L, booking));
    }



    @Test
    void createAvailableIsFalseTest() {
        item1.setAvailable(false);
        BookingCreateDto booking = new BookingCreateDto(1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                item1.getId());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(), booking));
    }

    @Test
    void createOwnerIsBookingTest() {
        Item item = new Item(1L, "item", "it", false, user2, null);
        BookingCreateDto thisBooking = new BookingCreateDto(1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                item.getId());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(user2.getId(), thisBooking));
    }

    @Test
    public void testCreateBooking() {
        Long userId = 1L;
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        BookingDto bookingDto = new BookingDto(bookingCreateDto.getId(), bookingCreateDto.getStart(),
                null, null, null, Status.WAITING);
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(new User());
        User user = new User();
        user.setId(2L);
        item.setOwner(user);

        when(itemRepository.findById(bookingCreateDto.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingMapper.toBooking(any())).thenReturn(booking);
        when(bookingMapper.toBookingDto(any())).thenReturn(bookingDto);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.create(userId, bookingCreateDto);

        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void updateBookingIdIsIncorrectTest() {
        assertThrows(NotFoundException.class,
                () -> bookingService.update(user1.getId(), 999L, true));
    }

    @Test
    void updateStatusIsAPPROVED() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(AccessException.class,
                () -> bookingService.update(user1.getId(), 1L, true));
    }

    @Test
    public void testUpdateBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        Item item = new Item();
        item.setId(1L);
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStatus(Status.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto updatedBooking = bookingService.update(bookingId, userId, true);

        assertEquals(bookingDto.getId(), updatedBooking.getId());
        assertEquals(bookingDto.getStatus(), updatedBooking.getStatus());
    }

    @Test
    void getByIdIdIsIncorrect() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getById(999L, 999L));
    }

    @Test
    void getByIdBookerOrOwnerIsGettingBooking() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getById(1L, user1.getId()));
    }

    @Test
    void getByIdTest() {
        BookingDto bookingDto = new BookingDto(booking.getId(), booking.getStart(),
                booking.getEnd(), null, null, Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(any())).thenReturn(bookingDto);

        BookingDto returnedBooking = bookingService.getById(booking.getId(), user1.getId());

        assertThat(returnedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void getBookingsByOwnerStateIsCurrentTest() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(user2.getId(),
                State.CURRENT, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwnerStateIsPastTest() {
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.of(2022, 12, 12, 12, 12, 0),
                LocalDateTime.of(2023, 1, 12, 12, 12, 0),
                item1, user1, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking1));
        when(bookingRepository.findAllByItemOwnerAndEndBefore(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(user2.getId(),
                State.PAST, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwnerStateIsFutureTest() {
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                item1, user1, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking1));
        when(bookingRepository.findAllByItemOwnerAndStartAfter(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(user2.getId(),
                State.FUTURE, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwnerStateIsWaitingTest() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByItemOwnerAndStatusEquals(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(user2.getId(),
                State.WAITING, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwnerStateIsRejectedTest() {
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                item1, user1, Status.REJECTED);
        Page<Booking> pages = new PageImpl<>(List.of(booking1));
        when(bookingRepository.findAllByItemOwnerAndStatusEquals(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(user2.getId(),
                State.REJECTED, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUseridIsIncorrectTest() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByUser(999L, State.ALL, 0, 10));
    }

    @Test
    void getBookingsByUserStateIsAllTest() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBooker(any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        List<BookingDto> bookings = bookingService.getBookingsByUser(user1.getId(),
                State.ALL, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUserStateIsCurrentTest() {
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.of(2023, 10, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                item1, user1, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking1));
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        List<BookingDto> bookings = bookingService.getBookingsByUser(user1.getId(),
                State.CURRENT, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUserStateIsPastTest() {
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.of(2022, 10, 12, 12, 12, 0),
                LocalDateTime.of(2023, 1, 12, 12, 12, 0),
                item1, user1, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking1));
        when(bookingRepository.findAllByBookerAndEndBefore(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        List<BookingDto> bookings = bookingService.getBookingsByUser(user1.getId(),
                State.PAST, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUserStateIsFutureTest() {
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                item1, user1, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking1));
        when(bookingRepository.findAllByBookerAndStartAfter(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        List<BookingDto> bookings = bookingService.getBookingsByUser(user1.getId(),
                State.FUTURE, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUserStateIsWaitingTest() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBookerAndStatusEquals(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        List<BookingDto> bookings = bookingService.getBookingsByUser(user1.getId(),
                State.WAITING, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUserStateIsRejectedTest() {
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                item1, user1, Status.REJECTED);
        Page<Booking> pages = new PageImpl<>(List.of(booking1));
        when(bookingRepository.findAllByBookerAndStatusEquals(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        List<BookingDto> bookings = bookingService.getBookingsByUser(user1.getId(),
                State.REJECTED, 0, 10);

        assertFalse(bookings.isEmpty());
    }
}
