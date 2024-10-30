package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.Constants;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestConnectStatServiceClient {
    private final StatClient statClient;

    @PostMapping
    public void saveHit(HttpServletRequest request) {
        statClient.save("main", request);
    }

    @GetMapping
    public List<ViewStats> getHit(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(required = false) boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, Constants.FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, Constants.FORMATTER);
        return statClient.getViewStats(startTime, endTime, uris, unique);
    }
}