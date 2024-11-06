package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(NewEventDto eventDto);

    EventFullDto updateTheEventByTheUser(long userId, long eventId, UpdateEventUserRequest dto);

    List<EventShortDto> getUserEvents(long userId, int from, int size);

    EventFullDto getUserFullEventBuId(long userId, long eventId);

    List<EventFullDto> getFullEventsForAdmin(List<Long> userIds, List<State> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventFromAdmin(long eventId, UpdateEventAdminRequest dto);

    List<EventShortDto> getAllShortEventsForPublicUsers(String text,
                                                        List<Long> categories,
                                                        Boolean paid,
                                                        LocalDateTime rangeStart,
                                                        LocalDateTime rangeEnd,
                                                        boolean onlyAvailable,
                                                        String sort,
                                                        int from,
                                                        int size);

    EventFullDto getFullEventByIdForPublicUsers(long eventId);
}