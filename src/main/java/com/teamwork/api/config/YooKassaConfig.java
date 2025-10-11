package com.teamwork.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.loolzaaa.youkassa.client.ApiClient;
import ru.loolzaaa.youkassa.client.ApiClientBuilder;

@Configuration
public class YooKassaConfig {

    @Bean
    public ApiClient apiClient() {
        return ApiClientBuilder.newBuilder()
                .configureBasicAuth("511077", "test_*gsCEr7l0WlmzmBNF9VSGyyzEEkVQPtXoHjjzMflLx_dM")
                .build();
    }
}
