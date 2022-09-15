package com.easytrade.easytradeapi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 微信支付工具类
 *
 * @author Mo Xu
 * @date 2022/07/14
 */
public class WechatPayUtil {
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
