package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.StatClient;
import ru.practicum.dto.Constants;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventServiceImpl;
    private final StatClient statClient;

    @GetMapping
    public List<EventShortDto> getAllShortEventsForPublicUsers(@RequestParam(required = false) String text,
                                                               @RequestParam(required = false) List<Long> categories,
                                                               @RequestParam(required = false) Boolean paid,
                                                               @RequestParam(required = false)
                                                               @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                                               LocalDateTime rangeStart,
                                                               @RequestParam(required = false)
                                                               @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                                               LocalDateTime rangeEnd,
                                                               @RequestParam(defaultValue = "false")
                                                               boolean onlyAvailable,
                                                               @RequestParam(required = false) String sort,
                                                               @RequestParam(defaultValue = "0") int from,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               HttpServletRequest request) {
        statClient.save("ewm-main-service", request);
        return eventServiceImpl.getAllShortEventsForPublicUsers(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getFullEventByIdForPublicUsers(@PathVariable long eventId, HttpServletRequest request) {
        statClient.save("ewm-main-service", request);
        return eventServiceImpl.getFullEventByIdForPublicUsers(eventId);
    }

}