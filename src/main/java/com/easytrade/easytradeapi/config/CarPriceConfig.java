/*
 * @Description: 广告价格配置类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-18 00:51:12
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-18 01:20:02
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/config/PriceConfig.java
 */
package com.easytrade.easytradeapi.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "price.car")
@Data
public class CarPriceConfig {
    private Map<String, Long> standard;

    private Map<String, Long> advanced;

    private Map<String, Long> elite;
}

