package com.easytrade.easytradeapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "price.motor")
@Data
public class MotorPriceConfig {
    private Map<String, Long> standard;

    private Map<String, Long> advanced;

    private Map<String, Long> elite;
}
