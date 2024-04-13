package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Item item;

    private User booker;

    private Status status;
}
