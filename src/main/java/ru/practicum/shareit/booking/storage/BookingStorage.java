package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.enums.Status;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndDateIsBefore(Long bookerId, Long itemId,
                                                                          Status status, LocalDateTime end);

    List<Booking> findAllByBookerAndStartDateBeforeAndEndDateAfter(User booker, LocalDateTime start,
                                                           LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerAndEndDateBefore(User booker, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerAndStartDateAfter(User booker, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerAndStatusEquals(User booker, Status status, Sort sort);

    List<Booking> findAllByItemOwner(User owner, Sort sort);

    List<Booking> findAllByItemOwnerAndStartDateBeforeAndEndDateAfter(User owner, LocalDateTime start,
                                                              LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndEndDateBefore(User owner, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndStartDateAfter(User owner, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdInAndStartDateLessThanEqualAndStatus(List<Long> idItems, LocalDateTime now,
                                                                        Status approved, Sort sort);

    Optional<Booking> findFirstByItemIdInAndStartDateAfterAndStatus(List<Long> idItems, LocalDateTime now,
                                                                Status approved, Sort sort);
}
