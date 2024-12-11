package com.example.domain.fund.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class OpenApiConfig {

    @Value("${korea-investment.api-key}")
    private String apiKey;

    @Value("${korea-investment.secret-key}")
    private String secretKey;

    @Value("${korea-investment.base-url}")
    private String baseUrl;

}
