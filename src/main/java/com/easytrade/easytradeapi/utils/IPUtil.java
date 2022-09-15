/*
 * @Description: IP工具类，用户获取调用接口的用户ip地址
 * @Author: Mo Xu
 * @Date: 2021-11-13 21:53:31
 * @LastEditors: Mo Xu
 * @LastEditTime: 2021-12-18 00:35:49
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/utils/IPUtil.java
 */
package com.easytrade.easytradeapi.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IPUtil {
    public static String getIpAddr() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces =
                    NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
