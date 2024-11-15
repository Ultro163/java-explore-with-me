package ru.practicum.like.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.like.model.EventLike;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    private Long userId;
    private EventLike eventLike;
}