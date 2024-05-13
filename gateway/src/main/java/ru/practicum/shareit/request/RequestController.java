package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";


    @Autowired
    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long requestId) {
        log.info("Get request with {} by user with id {}", requestId, userId);
        return requestClient.getById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @Valid @RequestBody RequestDto requestCreateDto) {
        log.info("Create request {} by user with id {}", requestCreateDto, userId);
        return requestClient.create(requestCreateDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwner(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get requests of user with id {}", userId);
        return requestClient.getByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                         @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Get all requests starting from {}," +
                " by {} item per page for user with id {}", from, size, userId);
        return requestClient.getAll(userId, from, size);
    }
}