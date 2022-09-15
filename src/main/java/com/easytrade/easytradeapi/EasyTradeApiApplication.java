package com.easytrade.easytradeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 简单贸易api应用程序
 *
 * @author xumo
 * @date 2022/03/12
 */
@SpringBootApplication
@EnableScheduling
public class EasyTradeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyTradeApiApplication.class, args);
    }

}
