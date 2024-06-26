package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto create(ItemRequestCreateDto request);

    ItemRequestDto getById(Long userId, Long requestId);

    List<ItemRequestDto> getByOwner(Long userId);

    List<ItemRequestDto> getRequests(Long userId, Pageable pageable);
}
