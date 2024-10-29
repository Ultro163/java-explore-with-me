package ru.practicum.error.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.exception.ValidationException;
import ru.practicum.error.model.ApiError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final Exception e) {
        log.warn(e.getMessage());
        String errorMessage;
        List<String> errors = new ArrayList<>();
        String reason;

        if (e instanceof MethodArgumentNotValidException ex) {
            errors = ex.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> String.format("Field '%s': %s", fieldError.getField(),
                            fieldError.getDefaultMessage()))
                    .collect(Collectors.toList());
            errorMessage = "Validation failed for one or more fields";
            reason = "MethodArgumentNotValidException";
        } else if (e instanceof MissingServletRequestParameterException ex) {
            errorMessage = String.format("Required parameter is missing: %s", ex.getParameterName());
            reason = "MissingServletRequestParameterException";
        } else {
            errorMessage = e.getMessage();
            reason = "ValidationException";
        }
        return ApiError.builder()
                .errors(errors)
                .message(errorMessage)
                .reason(reason)
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now())
                .build();
    }
}