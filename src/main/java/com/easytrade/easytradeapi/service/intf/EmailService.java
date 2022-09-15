/*
 * @Description: 邮件业务服务层的接口
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-08 21:57:00
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 22:24:43
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/EmailService.java
 */
package com.easytrade.easytradeapi.service.intf;

import javax.mail.MessagingException;


public interface EmailService {

    /**
     * 发送验证邮件
     *
     * @param token 用户的JWT
     * @param to    目标邮箱
     * @throws MessagingException 通讯异常
     */
    public void sendVerifyEmail(String token, String to) throws MessagingException;

    /**
     * @description: 验证邮箱方法，通过点击提供的链接调用此API来完成用户验证
     * @param {String} token 发送给用户邮箱中的JWT，用于校验
     * @return {*}
     */
    public void verifyEmail(String token);
}
