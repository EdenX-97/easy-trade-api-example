/*
 * @Description: MongoDB的事务开启配置累
 * @Author: Mo Xu
 * @Date: 2021-12-28 20:14:58
 * @LastEditors: Mo Xu
 * @LastEditTime: 2021-12-28 20:16:01
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/config/MongoTransactionConfig.java
 */
package com.easytrade.easytradeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class MongoTransactionConfig {
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }
}

