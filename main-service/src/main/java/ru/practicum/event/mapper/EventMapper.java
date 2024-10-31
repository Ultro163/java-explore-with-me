package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.model.Event;

@Mapper(uses = {CategoryMapper.class, EventMapper.class})
public interface EventMapper {

    Event toEntity(EventDto eventDto);

    EventDto toDto(Event event);
}