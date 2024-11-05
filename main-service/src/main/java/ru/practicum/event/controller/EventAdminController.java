package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.Constants;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventServiceImpl;

    @GetMapping
    public List<EventFullDto> getFullEventsForAdmin(@RequestParam(required = false) List<Long> userIds,
                                                    @RequestParam(required = false) List<State> states,
                                                    @RequestParam(required = false) List<Long> categories,
                                                    @RequestParam(required = false)
                                                    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                                    LocalDateTime rangeStart,
                                                    @RequestParam(required = false)
                                                    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                                    LocalDateTime rangeEnd,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {

        return eventServiceImpl.getFullEventsForAdmin(userIds, states, categories, rangeStart, rangeEnd, from, size);
    }


}