package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.Constants;
import ru.practicum.error.exception.ValidationException;
import ru.practicum.model.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.repository.EndpointHitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional
    public void save(EndpointHit endpointHit) {
        log.info("Create Endpoint Hit {}", endpointHit);
        EndpointHit resultEndpointHit = endpointHitRepository.save(endpointHit);
        log.info("Endpoint Hit created {}", resultEndpointHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getViewStats(String start, String end, List<String> uris, boolean unique) {
        log.info("Get view stats for {}", uris);
        LocalDateTime startTime = decodeTime(start);
        LocalDateTime endTime = decodeTime(end);

        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Start time must be not is after end time");
        }

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return endpointHitRepository.findAllViewStatsByBetweenTimestampAndUniqueIp(startTime, endTime);
            } else {
                return endpointHitRepository.findAllViewStatsByBetweenTimestamp(startTime, endTime);
            }
        } else {
            if (unique) {
                return endpointHitRepository
                        .findAllViewStatsByUrisAndBetweenTimestampAndUniqueIp(startTime, endTime, uris);
            } else {
                return endpointHitRepository.findAllViewStatsByUrisAndBetweenTimestamp(startTime, endTime, uris);
            }
        }
    }

    private LocalDateTime decodeTime(String time) {
        String decodeTime = URLDecoder.decode(time, StandardCharsets.UTF_8);
        return LocalDateTime.parse(decodeTime, Constants.FORMATTER);
    }
}