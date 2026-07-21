package com.example.carrentalsystem.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Keeps a free-tier host (e.g. Render) from spinning the service down.
 *
 * Render spins a free web service down after ~15 minutes with no inbound HTTP
 * traffic, and the next request then pays a ~50s cold-start. By pinging its own
 * public URL every few minutes, the service generates the inbound traffic that
 * resets that idle timer, so it stays warm and responses stay instant.
 *
 * Render injects RENDER_EXTERNAL_URL automatically. When that variable is not
 * present (local dev / docker-compose) the scheduler does nothing.
 */
@Component
public class KeepAliveScheduler {

    private static final Logger log = LoggerFactory.getLogger(KeepAliveScheduler.class);

    // Render sets this automatically to the service's public URL.
    @Value("${RENDER_EXTERNAL_URL:}")
    private String selfUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Every 10 minutes, after an initial 2-minute delay to let startup finish.
    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 2 * 60 * 1000)
    public void keepAwake() {
        if (selfUrl == null || selfUrl.isBlank()) {
            return; // Not running on a PaaS that needs keep-alive.
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(selfUrl + "/api/health"))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Keep-alive ping -> {}/api/health ({})", selfUrl, response.statusCode());
        } catch (Exception e) {
            log.warn("Keep-alive ping failed: {}", e.getMessage());
        }
    }
}
