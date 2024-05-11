package ru.practicum.shareit.Item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.model.AccessException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapping.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private User user;
    private ItemCreateDto itemCreateDto;
    private ItemDto itemDto;

    private UserCreateDto userCreateDto;
    private CommentCreateDto commentCreateDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUpTest() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        userCreateDto = new UserCreateDto();
        userCreateDto.setId(1L);
        userCreateDto.setName("userrr");
        userCreateDto.setEmail("userrrr@yande.ru");

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Test Comment");

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test Comment");
    }

    @Test
    @Transactional
    void addItemTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemCreateDto, user)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.addItem(1L, itemCreateDto);

        assertEquals(itemDto, result);
        verify(userRepository, times(1)).findById(1L);
        verify(itemMapper, times(1)).toItem(itemCreateDto, user);
        verify(itemRepository, times(1)).save(item);
        verify(itemMapper, times(1)).toItemDto(item);
    }

    @Test
    void createNullUserId() {
        assertThrows(NotFoundException.class,
                () -> itemService.addItem(null, new ItemCreateDto()));
    }

    @Test
    @Transactional
    void updateItemTest() {
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Old Name");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(false);
        User owner = new User();
        owner.setId(1L);
        existingItem.setOwner(owner);
        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setId(1L);
        itemDto.setUserId(1L);
        itemDto.setName("New Name");
        itemDto.setDescription("New Description");
        itemDto.setAvailable(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.toItemDto(existingItem)).thenReturn(new ItemDto());
        ItemDto result = itemService.updateItem(itemDto);
        verify(itemRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(itemMapper, times(1)).toItemDto(existingItem);
        assertEquals("New Name", existingItem.getName());
        assertEquals("New Description", existingItem.getDescription());
        assertTrue(existingItem.getAvailable());
    }

    @Test
    void getByIdTest() {
        User owner = new User(1L, "1", "a@yandex.ru");
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        ItemDto result = itemService.getById(1L, 1L);
        assertEquals(itemDto, result);
        verify(itemRepository, times(1)).findById(1L);
        verify(itemMapper, times(1)).toItemDto(item);
    }

    @Test
    void getUsersItemsTest() {
        Request request = new Request(1L, "req", new User(), LocalDateTime.now());
        User user = new User(1L, "username", "email@example.com");
        Item item = new Item(1L, "Item Name", "Item Description", true, request, user);
        Booking approvedBooking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user, Status.APPROVED);
        ItemDto itemDto = new ItemDto();
        Comment comment1 = new Comment(1L, "Comment 1", LocalDateTime.now());
        comment1.setItem(item);
        comment1.setAuthor(user);
        List<Comment> allComments = Collections.singletonList(comment1);
        CommentDto commentDto1 = new CommentDto();

        Pageable pageable = PageRequest.of(0, 10, Sort.by(DESC, "start"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findItemByOwnerId(1L, pageable))
                .thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(bookingRepository.findAllByItemInAndStatus(Collections.singletonList(item), Status.APPROVED))
                .thenReturn(Collections.singletonList(approvedBooking));
        when(commentRepository.findAllByItemIn(Collections.singletonList(item))).thenReturn(allComments);
        when(commentMapper.toCommentDto(comment1)).thenReturn(commentDto1);

        List<ItemDto> result = itemService.getUsersItems(1L, pageable);

        assertEquals(1, result.size());
        assertEquals(itemDto, result.get(0));
        assertEquals(1, result.get(0).getComments().size());
        assertEquals(commentDto1, result.get(0).getComments().get(0));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findItemByOwnerId(1L, pageable);
        verify(itemMapper, times(1)).toItemDto(item);
        verify(bookingRepository, times(1))
                .findAllByItemInAndStatus(Collections.singletonList(item), Status.APPROVED);
        verify(commentRepository, times(1)).findAllByItemIn(Collections.singletonList(item));
        verify(commentMapper, times(1)).toCommentDto(comment1);
    }


    @Test
    void removeByIdTest() {
        doNothing().when(itemRepository).deleteById(1L);

        itemService.removeById(1L, 1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void searchTest() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(DESC, "start"));
        when(itemRepository.search("test", "test", true, pageable))
                .thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        List<ItemDto> result = itemService.search("test", pageable);
        assertEquals(1, result.size());
        assertEquals(itemDto, result.get(0));
        verify(itemRepository, times(1))
                .search("test", "test", true, pageable);
        verify(itemMapper, times(1)).toItemDto(item);
    }

    @Test
    void testSearchWithBlankText() {
        String text = "   ";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(DESC, "start"));

        when(itemRepository.search(text, text, true, pageable))
                .thenReturn(Collections.singletonList(item));

        List<ItemDto> result = itemService.search(text, pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_success() {
        Long userId = 1L;
        Long itemId = 2L;
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        User user = new User();
        Item item = new Item();
        Comment comment = new Comment();
        CommentDto commentDto = new CommentDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.toComment(commentCreateDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                any(), any(), any(), any())).thenReturn(bookings);

        CommentDto result = itemService.addComment(itemId, userId, commentCreateDto);

        assertEquals(commentDto, result);
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(commentMapper).toComment(commentCreateDto);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toCommentDto(comment);
    }

    @Test
    void createCommentIdIsIncorrectTest() {
        assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, 500L, commentCreateDto));
    }

    @Test
    void createIncorrectIdTest() {
        assertThrows(NotFoundException.class,
                () -> itemService.addComment(500L, 1L, commentCreateDto));
    }

    @Test
    void createThrowExceptionIdIsIncorrectTest() {
        assertThrows(NotFoundException.class,
                () -> itemService.addItem(999L, itemCreateDto));
    }

    @Test
    void updateIdIsIncorrectTest() {
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(itemCreateDto));
    }

    @Test
    void addCommentThrowsAccessExceptionTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentMapper.toComment(commentCreateDto)).thenReturn(new Comment());
        when(commentRepository.save(any(Comment.class))).thenThrow(new AccessException("Access error"));

        assertThrows(AccessException.class, () -> itemService.addComment(1L, 1L, commentCreateDto));

        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findById(1L);
    }


    @Test
    void addCommentAccessExceptionTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                1L, 1L, Status.APPROVED, LocalDateTime.now())).thenReturn(Collections.emptyList());

        assertThrows(AccessException.class, () -> itemService.addComment(1L, 1L, commentCreateDto));
    }

    @Test
    void addCommentNotFoundExceptionTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 1L, commentCreateDto));
    }
}
