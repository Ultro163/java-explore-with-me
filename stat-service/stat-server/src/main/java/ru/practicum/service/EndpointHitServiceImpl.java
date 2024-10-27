package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.EndpointHitRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    public void create(EndpointHit endpointHit) {
        log.info("Create Endpoint Hit {}", endpointHit);
        EndpointHit resultEndpointHit = endpointHitRepository.save(endpointHit);
        log.info("Endpoint Hit created {}", resultEndpointHit);
    }
}