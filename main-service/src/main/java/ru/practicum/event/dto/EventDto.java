package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    @NotNull
    private Long id;
    @NotNull
    private String annotation;
    @NotNull
    private CategoryDto category;
    private Integer confirmedRequest;
    @NotNull
    private OffsetDateTime eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Boolean paid;
    @NotNull
    @NotBlank
    private String title;
    private Integer views;
}