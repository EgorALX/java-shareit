package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final RequestMapper mapper;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto request, LocalDateTime created) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Request newRequest = mapper.toRequest(request, userId, created);
        return mapper.toRequestDto(requestRepository.save(newRequest));
    }

    @Override
    public List<ItemRequestDto> getByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        return requestRepository.findAllByRequesterId(userId, Sort.by(DESC, "created"))
                .stream().map(mapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        int pageNumber = (from == null ? 0 : from);
        int pageSize = (size == null ? 500 : size);
        Sort sort = Sort.by(DESC, "created");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Request> page = requestRepository.findAllByRequesterIdNot(userId, pageable);
        return page.getContent().stream()
                .map(mapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request " + requestId + " not found"));
        return mapper.toRequestDto(request);
    }
}