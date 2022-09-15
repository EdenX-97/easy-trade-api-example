/*
 * @Description: Java Web Token(JWT)的配置类
 * @Author: Mo Xu
 * @Date: 2021-11-04 00:01:03
 * @LastEditors: Mo Xu
 * @LastEditTime: 2021-12-31 15:40:36
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/config/JWTConfig.java
 */
package com.easytrade.easytradeapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;


@Configuration
@ConfigurationProperties(prefix = "jwt") // 读取来自application.properties文件中jwt字段下的配置
@Data
public class JWTConfig {
    // 所有Token的密文，用于校验某个Token是否合法 
    private String secret;

    // Token的过期时间，一般为3天
    private int expiration;

    // Token的前缀，一般为Bearer后跟一个空格
    private String prefix;

    // Token存储在Header中的Key名，一般为Authorization
    private String authorization;
}
