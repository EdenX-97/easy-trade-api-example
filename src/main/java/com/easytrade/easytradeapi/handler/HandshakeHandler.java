/*
 * @Description: 
 * @Author: Mo Xu
 * @Date: 2022-01-11 01:14:23
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-11 01:14:23
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/HandshakeHandler.java
 */
/*
 * @Description: 自定义握手处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 23:16:48
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-10 23:34:14
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/HandshakeHandler.java
 */
package com.easytrade.easytradeapi.handler;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class HandshakeHandler extends DefaultHandshakeHandler {
    @Override
    public Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        // 设置地址如：wss://localhost:8080/websocket/1 
        // 格式为 wss://xxx/websocket/{userId}
        String uri = request.getURI().toString();
        String uid = uri.substring(uri.lastIndexOf("/") + 1);
        return () -> uid;
    }

    
}

