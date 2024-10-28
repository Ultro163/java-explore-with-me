package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dto.Constants.FORMATTER;

@Slf4j
@Component
public class StatClient {
    private final RestClient client;

    public StatClient(@Value("${stat-server.url}") String serverUrl) {
        this.client = RestClient.create(serverUrl);
        log.info("Stat-server run URL: {}", serverUrl);
    }

    public void save(String app, HttpServletRequest request) {
        log.info("Saving hit for app: {}", app);
        EndpointHitDto dto = getDto(app, request);
        ResponseEntity<Void> response = client.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve().toBodilessEntity();
        log.info("Response status: {}", response.getStatusCode());
    }

    public ResponseEntity<List<ViewStats>> getViewStats(LocalDateTime start, LocalDateTime end,
                                                        List<String> uris, boolean unique) {
        log.info("Getting view stats for uri: {}", uris);
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.format(FORMATTER))
                        .queryParam("end", end.format(FORMATTER))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        ((request, response) -> log.error("Getting stats for {} with error code {}", uris,
                                response.getStatusCode())))
                .body(new ParameterizedTypeReference<>() {});
    }

    private EndpointHitDto getDto(String app, HttpServletRequest request) {
        return EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}