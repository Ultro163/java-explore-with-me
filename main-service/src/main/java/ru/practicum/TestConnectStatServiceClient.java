package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestConnectStatServiceClient {
    private final StatClient statClient;

    @PostMapping
    public void saveHit(HttpServletRequest request) {
        statClient.save("main", request);
    }
}
