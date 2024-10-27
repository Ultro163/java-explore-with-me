package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper
public interface EndpointHitMapper {
    EndpointHit toEntity(EndpointHitDto endpointHitDto);
}