// RestTemplate 빈 등록 (예: src/main/java/com/ms/reborn/config/AppConfig.java)
package com.ms.reborn.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
