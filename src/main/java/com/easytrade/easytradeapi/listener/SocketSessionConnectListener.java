/*
 * @Description: websocket建立连接事件监听类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 18:54:39
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 22:32:51
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/listener/SocketSessionConnectListener.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/listener/SocketSessionConnectListener.java
 */
package com.easytrade.easytradeapi.listener;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.ChatException;
import com.easytrade.easytradeapi.repository.ChatRecordRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.ChatService;
import com.easytrade.easytradeapi.utils.SocketSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;


@Component
public class SocketSessionConnectListener extends BaseSessionEventListener<SessionConnectEvent> {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatService chatService;

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        using(event, (userId, sessionId) -> {
            // 检验用户是否存在
            if (!chatService.checkUserExist(userId)) {
                throw new ChatException(ResultCodeEnum.NOT_FOUND, "User not exist");
            }

            // 注册session
            SocketSessionUtil.registerSessionId(userId, sessionId);
        });
    }
}

