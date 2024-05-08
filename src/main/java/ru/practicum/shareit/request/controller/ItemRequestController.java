package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

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
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setRequesterId(userId);
        ItemRequestDto result = service.create(itemRequestDto);
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
        int pageNumber = (from == null ? 0 : from);
        int pageSize = (size == null ? 500 : size);
        Sort sort = Sort.by(DESC, "created");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        List<ItemRequestDto> result = service.getRequests(userId, pageable);
        log.info("All requests retrieved successfully for userId: {} with pagination from: {} and size: {}", userId, from, size);
        return result;
    }
}
