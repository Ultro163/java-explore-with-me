package ru.practicum.event.dto.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.dto.mapper.CategoryMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.mapper.UserMapper;

@Mapper(uses = {CategoryMapper.class, EventMapper.class, UserMapper.class})
public interface EventMapper {
    @Mapping(source = "initiator", target = "initiator.id")
    @Mapping(source = "category", target = "category.id")
    Event toEntity(NewEventDto newEventDto);

    Event toEntity(EventFullDto eventFullDto);

    @InheritInverseConfiguration(name = "toEntity")
    EventFullDto toDto(Event event);

    Location toEntity(LocationDto locationDto);

    @InheritInverseConfiguration(name = "toEntity")
    LocationDto toDto(Location location);

    Event toEntity(EventShortDto eventShortDto);

    @InheritInverseConfiguration(name = "toEntity")
    EventShortDto toShortDto(Event event);

    @Mapping(source = "categoryId", target = "category.id")
    Event toEntity(UpdateEventAdminRequest updateEventAdminRequest);

}