package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateDto {

    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Boolean available;

    private User owner;

    private Long userId;
}
