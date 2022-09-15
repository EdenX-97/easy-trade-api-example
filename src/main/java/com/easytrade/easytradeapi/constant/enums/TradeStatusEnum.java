/*
 * @Description: 交易状态枚举类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-19 01:26:51
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 04:02:07
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/constant/enums/TradeStatusEnum.java
 */

package com.easytrade.easytradeapi.constant.enums;

public enum TradeStatusEnum {
    PAYING, // 支付中
    PAID, // 已支付
    FREE, // 使用了免费广告
    REFUNDING, // 退款中
    REFUNDED, // 已退款
    CLOSED // 已关闭
}
