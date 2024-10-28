package ru.practicum;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class StatClient {
    private final RestClient client;

    public StatClient(@Value("${stat-server.url}") String serverUrl) {
        this.client = RestClient.create(serverUrl);
        log.info("Stat-server run URL: {}", serverUrl);
    }
}