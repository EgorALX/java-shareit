package ru.practicum.shareit.item.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class ItemMapper {

    private final RequestRepository requestRepository;

    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public Item toItem(ItemCreateDto item, User owner) {
        return new Item(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequestId() != null ? requestRepository.findById(item.getRequestId()).orElse(null) : null, owner);
    }
}
