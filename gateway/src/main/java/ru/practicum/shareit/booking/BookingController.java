package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
										 @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {} by userId={}", requestDto, userId);
		return bookingClient.create(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) long userId,
										 @PathVariable long bookingId, @RequestParam String approved) {
		log.info("Update booking {} by user {} to {}",
				bookingId, userId, approved);
		return bookingClient.update(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {} by userId={}", bookingId, userId);
		return bookingClient.getById(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingByOwner(@RequestHeader(USER_ID_HEADER) long userId,
												@RequestParam(name = "state", defaultValue = "all") String stateParam,
												@RequestParam(defaultValue = "0") @Min(0) int from,
												@RequestParam(defaultValue = "10") @Min(1) int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking for item owner with state {}, userId={}, from={}, size={}",
				userId, stateParam, from, size);
		return bookingClient.getBookingByOwner(userId, state, from, size);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
													@RequestParam(name = "state", defaultValue = "all") String stateParam,
													@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
													@Positive @RequestParam(name = "size", defaultValue = "10") int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookingsByUser(userId, state, from, size);
	}
}
