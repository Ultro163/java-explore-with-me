package ru.practicum.request.dto.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

@Mapper
public interface RequestMapper {
    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "eventId", target = "event.id")
    Request toEntity(RequestDto requestDto);

    @InheritInverseConfiguration(name = "toEntity")
    RequestDto toDto(Request request);
}