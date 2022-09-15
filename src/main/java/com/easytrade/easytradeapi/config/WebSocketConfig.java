/*
 * @Description: WebSocket的配置类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 21:58:27
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 16:46:49
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/config/WebSocketConfig.java
 */
package com.easytrade.easytradeapi.config;

import com.easytrade.easytradeapi.handler.HandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chat");
    }

    // 注册连接点
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket/{id}").setHandshakeHandler(new HandshakeHandler())
                .setAllowedOriginPatterns("*");
    }
}

