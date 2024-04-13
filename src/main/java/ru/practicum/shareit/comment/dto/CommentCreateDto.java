package ru.practicum.shareit.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentCreateDto {

    private Long id;

    @NotBlank
    private String text;

    private String authorName;

    private LocalDateTime created;

}
