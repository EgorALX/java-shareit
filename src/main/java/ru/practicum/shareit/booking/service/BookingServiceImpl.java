package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.mapping.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;

    private final UserStorage userStorage;

    private final ItemStorage itemStorage;

    private final BookingMapper mapper;

    private static final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) throws Exception {
        Item item = itemStorage.findById(bookingCreateDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Data not found"));
        if (item.getAvailable().equals(false)) {
            throw new Exception("Item not available");
        }
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found"));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Data not found");
        }
        if (bookingCreateDto.getStartDate().equals(bookingCreateDto.getEndDate())) {
            throw new ValidationException("The date of creation and the end of the reservation must be different");
        }
        if (bookingCreateDto.getEndDate().isBefore(bookingCreateDto.getStartDate())
                || bookingCreateDto.getEndDate() == bookingCreateDto.getStartDate()) {
            throw new ValidationException("Ошибка валидации времени");
        }
        Booking thisBooking = mapper.toBooking(bookingCreateDto);
        thisBooking.setItem(item);
        thisBooking.setBooker(user);
        thisBooking.setStatus(Status.WAITING);
        Booking savedBooking = bookingStorage.save(thisBooking);
        return mapper.toBookingDto(savedBooking);
    }

    @Transactional
    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) throws Exception {
        Booking thisBooking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование не найдено"));
        if (!userId.equals(thisBooking.getItem().getOwner().getId())) {
            throw new NotFoundException("Не найдено");
        }
        if (!thisBooking.getStatus().equals(Status.WAITING)) {
            throw new Exception("Нельзя изменить статус");
        }
        thisBooking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return mapper.toBookingDto(thisBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование не найдено"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Неверный запрос");
        }
        return mapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, State state) throws Exception {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingStorage.findAllByItemOwner(user, sort);
                break;
            case CURRENT:
                bookingList = bookingStorage.findAllByItemOwnerAndStartDateBeforeAndEndDateAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                bookingList = bookingStorage.findAllByItemOwnerAndEndDateBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingList = bookingStorage.findAllByItemOwnerAndStartDateAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingList = bookingStorage.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingStorage.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new Exception("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, State state) throws Exception {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingStorage.findAllByBooker(user, sort);
                break;
            case CURRENT:
                bookingList = bookingStorage.findAllByBookerAndStartDateBeforeAndEndDateAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), sort);
                break;
            case PAST:
                bookingList = bookingStorage.findAllByBookerAndEndDateBefore(user,
                        LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingList = bookingStorage.findAllByBookerAndStartDateAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingList = bookingStorage.findAllByBookerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingStorage.findAllByBookerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new Exception("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList());
    }
}
