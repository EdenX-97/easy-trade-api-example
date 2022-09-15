/*
 * @Description: 取消广告原因的枚举类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-14 19:08:22
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-14 19:09:23
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/constant/enums/CancelAdReasonEnum.java
 */
package com.easytrade.easytradeapi.constant.enums;

public enum CancelAdReasonEnum {
    ACTIVELY, // 用户主动取消
    OVERONEYEAR, // 广告存在时间超过一年取消
    NOLOGIN // 用户超过一定时间没有登录取消
}

