package com.teamwork.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.loolzaaa.youkassa.client.ApiClient;
import ru.loolzaaa.youkassa.client.ApiClientBuilder;

@Configuration
public class YooKassaConfig {

    @Value("${payment.yookassa.shop-id}")
    private String shopId;

    @Value("${payment.yookassa.secret-key}")
    private String secretKey;

    @Bean
    public ApiClient apiClient() {
        return ApiClientBuilder.newBuilder()
                .configureBasicAuth("1186100", "test_4smhd--N4gYns3DT4ZIH39a7W1DXTfYvjztahe0ZDKk")
                .build();
    }
}
