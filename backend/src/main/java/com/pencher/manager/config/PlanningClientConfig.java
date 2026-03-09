package com.pencher.manager.config;

import com.pencher.manager.integration.MockPlanningApiClient;
import com.pencher.manager.integration.PlanningApiClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlanningClientConfig {

    @Bean
    @ConditionalOnProperty(name = "pencher.planning.use-mock", havingValue = "true", matchIfMissing = true)
    public PlanningApiClient mockPlanningApiClient() {
        return new MockPlanningApiClient();
    }
}
