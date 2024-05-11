package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Boolean available;

    private Long userId;

    private Long requestId;
}