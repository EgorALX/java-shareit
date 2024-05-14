package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class BookItemRequestDto {

	private long itemId;

	@FutureOrPresent
	private LocalDateTime start;

	@Future
	private LocalDateTime end;

	@AssertTrue(message = "Time validation error")
	private boolean isTimeValid() {
		return !(start.equals(end) || end.isBefore(start));
	}
}
