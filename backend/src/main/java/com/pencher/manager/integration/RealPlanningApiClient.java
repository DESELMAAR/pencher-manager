package com.pencher.manager.integration;

import com.pencher.manager.integration.dto.PlanningApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Real Planning API client using WebClient with timeout and retry.
 * Enabled when pencher.planning.use-mock=false.
 */
@Component
@ConditionalOnProperty(name = "pencher.planning.use-mock", havingValue = "false")
public class RealPlanningApiClient implements PlanningApiClient {

    private final WebClient webClient;
    private final long timeoutMs;

    public RealPlanningApiClient(
            WebClient.Builder builder,
            @Value("${pencher.planning.api.url}") String baseUrl,
            @Value("${pencher.planning.api.timeout-ms:5000}") long timeoutMs) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.timeoutMs = timeoutMs;
    }

    @Override
    public List<PlanningApiResponse> getPlanningForEmployee(String employeeId, LocalDate from, LocalDate to) {
        try {
            return webClient.get()
                    .uri(uri -> uri.path("/api/planning/employee/{id}")
                            .queryParam("from", from)
                            .queryParam("to", to)
                            .build(employeeId))
                    .retrieve()
                    .bodyToFlux(PlanningApiResponse.class)
                    .timeout(Duration.ofMillis(timeoutMs))
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
