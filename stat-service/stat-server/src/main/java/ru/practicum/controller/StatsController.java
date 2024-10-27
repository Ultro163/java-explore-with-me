package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.service.EndpointHitService;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final EndpointHitService endpointHitServiceImpl;
    private final EndpointHitMapper endpointHitMapper;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody EndpointHitDto dto) {
        endpointHitServiceImpl.create(endpointHitMapper.toEntity(dto));
    }
}