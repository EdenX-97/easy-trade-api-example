/*
 * @Description: 支付宝工具类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-08 01:50:45
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-20 16:49:40
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/easytrade/api/utils/AlipayUtil.java
 */
package com.easytrade.easytradeapi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;


public class AlipayUtil {
    /**
     * @description: 生成随机的订单号
     * @param {*}
     * @return {String} 生成的订单号
     */    
    public static String generateTradeNo() {
        // 17位时间
        String tradeNo = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        // 加上6位随机数
        Random randomNum = new Random();
        for (int i = 0; i < 6; i++) {
            // 取大于等于0，小于10的伪随机数
            tradeNo += randomNum.nextInt(10);
        }

        // 23位订单号
        return tradeNo;
    }

    public static Boolean validateTradeNo(String tradeNo) {
        if (!Pattern.matches("^[0-9]{23}", tradeNo)) {
            return false;
        }
        String date = tradeNo.substring(0, 16);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date tradeDate = null;
        try {
            tradeDate = format.parse(date);
        } catch (Exception e) {
            return false;
        }
        
        Date nowDate = new Date();

        if (tradeDate.after(nowDate)) {
            return false;
        }
        
        return true;
    }
}

