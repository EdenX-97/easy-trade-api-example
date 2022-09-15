/*
 * @Description: 手机短信服务层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 19:26:25
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-05 20:19:10
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/ShortMessageService.java
 */
package com.easytrade.easytradeapi.service.intf;

import com.easytrade.easytradeapi.constant.consists.Result;

public interface ShortMessageService {
    /**
     * @description: 发送验证短信
     * @param {String} phone 需要验证的手机号
     * @return {*}
     */
    public void sendVerifyMessage(String phone);

    /**
     * @description: 验证手机号
     * @param {String} phone 需要验证的手机号
     * @param {String} verifyCode 收到的验证码
     * @return {*}
     */
    public void verifyPhone(String phone, String verifyCode);

    /**
     * @description: 获取发送的验证码的状态
     * @param {String} phone 发送的手机号
     * @param {String} date 发送时间
     * @return {String} 返回的状态信息
     */    
    public String getVerifyCodeStatus(String phone, String date);
}

