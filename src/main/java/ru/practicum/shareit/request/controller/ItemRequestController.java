package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final RequestService service;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID_HEADER) long userId,
                                 @Valid @RequestBody ItemRequestCreateDto itemRequestDto) {
        log.info("Creating a new request for userId: {}", userId);
        ItemRequestDto result = service.create(userId, itemRequestDto, LocalDateTime.now());
        log.info("Request created successfully for userId: {}", userId);
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable("requestId") Long requestId) {
        log.info("Getting request by id: {} for userId: {}", requestId, userId);
        ItemRequestDto result = service.getById(userId, requestId);
        log.info("Request retrieved successfully by id: {} for userId: {}", requestId, userId);
        return result;
    }

    @GetMapping
    public List<ItemRequestDto> getByOwner(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Getting requests by owner for userId: {}", userId);
        List<ItemRequestDto> result = service.getByOwner(userId);
        log.info("Requests retrieved successfully by owner for userId: {}", userId);
        return result;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Getting all requests for userId: {} with pagination from: {} and size: {}", userId, from, size);
        List<ItemRequestDto> result = service.getRequests(userId, from, size);
        log.info("All requests retrieved successfully for userId: {} with pagination from: {} and size: {}", userId, from, size);
        return result;
    }
}
