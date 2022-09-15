/*
 * @Description: 会话事件监听类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 18:45:03
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 18:47:20
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/listener/BaseSessionEventListener.java
 */
package com.easytrade.easytradeapi.listener;

import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

public abstract class BaseSessionEventListener<Event extends AbstractSubProtocolEvent>
        implements ApplicationListener<Event> {
    
    public void using(Event event, BiConsumer<String, String> biConsumer) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        List<String> shaNativeHeader = sha.getNativeHeader("token");
        String user;
        if (shaNativeHeader == null || shaNativeHeader.isEmpty()) {
            user = null;
        } else {
            user = shaNativeHeader.get(0);
        }
        String sessionId = sha.getSessionId();
        biConsumer.accept(user, sessionId);
    }
}
