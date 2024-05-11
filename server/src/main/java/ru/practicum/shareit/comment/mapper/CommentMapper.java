package ru.practicum.shareit.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

@Component
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(),
                comment.getCreatedDate());
    }

    public Comment toComment(CommentCreateDto comment) {
        return new Comment(comment.getId(), comment.getText(), comment.getCreated());
    }

}