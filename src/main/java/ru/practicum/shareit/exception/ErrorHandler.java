package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundEcxeption(final NotFoundException exception) {
        log.info("Данные не найдены {}", exception.getMessage());
        StringWriter error = new StringWriter();
        exception.printStackTrace(new PrintWriter(error));
        return Map.of("Данные не найдены ", exception.getMessage(), "StackTrace: ",
                error.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException exception) {
        return Map.of("Validation error: ", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicationException(final DuplicationException exception) {
        return Map.of("Duplication error: ", exception.getMessage());
    }
}
