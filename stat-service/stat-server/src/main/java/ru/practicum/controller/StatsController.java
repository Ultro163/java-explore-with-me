package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.EndpointHitService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final EndpointHitService endpointHitServiceImpl;
    private final EndpointHitMapper endpointHitMapper;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody EndpointHitDto dto) {
        endpointHitServiceImpl.save(endpointHitMapper.toEntity(dto));
    }

    @GetMapping("/stats")
    public List<ViewStats> getViewStats(@RequestParam String start,
                                        @RequestParam String end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(required = false) boolean unique) {
        return endpointHitServiceImpl.getViewStats(start, end, uris, unique);
    }
}