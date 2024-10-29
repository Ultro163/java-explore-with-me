package ru.practicum.service;

import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.util.List;

public interface EndpointHitService {
    void save(EndpointHit dto);

    List<ViewStats> getViewStats(String start, String end, List<String> uris, boolean unique);
}