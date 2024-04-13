package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    private Long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime startDate;
    @NotNull
    @Future
    private LocalDateTime endDate;
    @NotNull
    private Long itemId;
}
