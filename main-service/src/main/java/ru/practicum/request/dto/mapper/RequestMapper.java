package ru.practicum.request.dto.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

@Mapper
public interface RequestMapper {

    @Mapping(source = "requester", target = "requester.id")
    @Mapping(source = "event", target = "event.id")
    Request toEntity(ParticipationRequestDto participationRequestDto);

    @InheritInverseConfiguration(name = "toEntity")
    ParticipationRequestDto toDto(Request request);

}