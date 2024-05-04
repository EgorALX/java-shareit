package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {
    ItemRequestDto create(Long userId, ItemRequestCreateDto request, LocalDateTime localDateTime);

    ItemRequestDto getById(Long userId, Long requestId);

    List<ItemRequestDto> getByOwner(Long userId);

    List<ItemRequestDto> getRequests(Long userId, Pageable pageable);
}
