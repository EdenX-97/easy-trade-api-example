/*
 * @Description: websocket断开连接事件监听类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 18:56:39
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 21:45:07
 * 
 * @FilePath:
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/listener/SocketSessionDisconnectListener.java
 */
package com.easytrade.easytradeapi.listener;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.ChatException;
import com.easytrade.easytradeapi.service.intf.ChatService;
import com.easytrade.easytradeapi.utils.SocketSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@Component
public class SocketSessionDisconnectListener
        extends BaseSessionEventListener<SessionDisconnectEvent> {
    @Autowired
    ChatService chatService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        using(event, (userId, sessionId) -> {
            // // 检查用户是否在线
            // System.out.println(chatService.checkUserOnline(sessionId));
            // if (!chatService.checkUserOnline(sessionId)) {
            //     throw new ChatException(ResultCodeEnum.FAILED, "Cannot disconnect");
            // }

            SocketSessionUtil.removeSessionId(sessionId);
        });
    }
}

