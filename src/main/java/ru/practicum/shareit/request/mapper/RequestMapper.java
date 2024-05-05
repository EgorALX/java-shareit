package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RequestMapper {

    private final UserMapper userMapper;

    private final UserService userService;

    private final ItemService itemService;

    public Request toRequest(ItemRequestCreateDto request) {
        return new Request(
                null,
                request.getDescription(),
                userMapper.toUser(userService.getById(request.getRequesterId())),
                request.getCreated());
    }

    public ItemRequestDto toRequestDto(Request request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemService.getItemsByRequestId(request.getId()));
    }
}