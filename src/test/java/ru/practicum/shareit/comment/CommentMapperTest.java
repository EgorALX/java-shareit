package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapping.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CommentMapper commentMapper;


    @Test
    void toCommentDtoTest() {
        Comment comment = new Comment(1L, "text", new Item(1L, "11", "11", true, 1L, new User()),
                new User(1L, "Author", "ww@mail.ru"), LocalDateTime.now());
        CommentDto expectedDto = new CommentDto(1L, "text", "Author", comment.getCreatedDate());

        CommentDto actualDto = commentMapper.toCommentDto(comment);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void toCommentTest() {
        CommentCreateDto commentDto = new CommentCreateDto(1L, "text", " asa", LocalDateTime.now());
        Comment comment = new Comment(1L, "text", null, null, commentDto.getCreated());

        Comment actualComment = commentMapper.toComment(commentDto);

        assertEquals(comment, actualComment);
    }
}