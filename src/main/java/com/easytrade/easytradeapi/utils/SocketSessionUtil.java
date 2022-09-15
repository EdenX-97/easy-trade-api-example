/*
 * @Description: 用户websocket的session记录工具类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 18:48:38
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 21:22:56
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/utils/SocketSessionUtil.java
 */
package com.easytrade.easytradeapi.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketSessionUtil {
    // 储存用户名和simpSessionId
    private static final ConcurrentHashMap<String, String> userSession = new ConcurrentHashMap<>();

    /**
     * @description: 根据用户获取simpSessionId
     * @param {String} user 用户id
     * @return {String} 用户的simpSessionId
     */
    public static String getSessionId(String user) {
        return SocketSessionUtil.userSession.get(user);
    }

    /**
     * @description: 根据用户simpSessionId获取用户id
     * @param {String} sessionId 用户simpSessionId
     * @return {String} 用户id
     */
    public static String getUserId(String sessionId) {
        Iterator it = SocketSessionUtil.userSession.entrySet().iterator();
        String key = "";
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            if (entry.getValue().equals(sessionId)) {
                key = entry.getKey().toString();
            }
        }
        return key;
    }

    /**
     * @description: 根据用户id记录sessionId
     * @param {String} user 用户id
     * @param {String} sessionId simpSessionId
     * @return {*}
     */
    public static void registerSessionId(String user, String sessionId) {
        SocketSessionUtil.userSession.put(user, sessionId);
    }

    /**
     * @description: 根据session删除用户记录
     * @param {String} sessionId simpSessionId
     * @return {*}
     */
    public static void removeSessionId(String sessionId) {
        SocketSessionUtil.userSession.entrySet().stream()
                .filter(entry -> entry.getValue().equals(sessionId)).forEach(entry -> {
                    SocketSessionUtil.userSession.remove(entry.getKey());
                });
    }

    /**
     * @description: 获取记录
     * @param {*}
     * @return {ConcurrentHashMap<String, String>} 对应记录
     */    
    public static ConcurrentHashMap<String, String> getUserSession() {
        return SocketSessionUtil.userSession;
    }
}

