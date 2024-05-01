package ru.practicum.shareit.Item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ShareItApp.class)
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {

    @Mock
    private ItemRepository itemRepository;

    @Test
    void throwNotFoundExceptionTest() {
        ItemService itemService = new ItemServiceImpl(itemRepository, null,
                null, null, null, null, null);
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getById(1L, 999L));
    }
}
