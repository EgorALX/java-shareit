package ru.practicum.shareit.booking.model;

import lombok.Getter;
import ru.practicum.shareit.exception.model.ValidationException;

@Getter
public class Pagination {

    private Integer pageSize;

    private Integer index;

    private Integer totalPages;


    public Pagination(Integer from, Integer size) {
        validateInput(from, size);
        pageSize = from;
        index = 1;
        if (from == 0) {
            pageSize = size;
            index = 0;
        }
        totalPages = index + 1;
        if ((from < size) && (from != 0)) {
            totalPages = size / from + index;
            if (size % from != 0) {
                totalPages++;
            }
        }
    }

    private void validateInput(Integer from, Integer size) {
        if (size != null) {
            if (from < 0) {
                throw new ValidationException("from must be greater than or equal to zero.");
            }
            if (size <= 0) {
                throw new ValidationException("size must be greater than zero.");
            }
        }
    }

}