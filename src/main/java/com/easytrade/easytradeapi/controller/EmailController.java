/*
 * @Description: 邮件控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 19:24:56
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 22:25:20
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/EmailController.java
 */
package com.easytrade.easytradeapi.controller;

import javax.mail.MessagingException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.service.intf.EmailService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class EmailController {
    @Autowired
    EmailService emailService;

    /**
     * @description: 发送验证邮件
     * @param {String} account 用户账号
     * @param {String} to 收件者邮箱，调用接口的用户邮箱
     * @return {Result} 返回结果
     */
    @PostMapping("/email/sendVerifyEmail")
    public Result sendVerifyEmail(@RequestBody @NotNull @NotBlank String account,
            @RequestBody @NotNull @NotBlank String to) throws MessagingException {
        emailService.sendVerifyEmail(account, to);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 验证邮箱方法，通过点击提供的链接调用此API来完成用户验证
     * @param {String} token 发送给用户邮箱中的JWT，用于校验
     * @return {Result} 返回结果
     */
    @PostMapping("/email/verifyEmail")
    public Result verifyEmail(@RequestParam @NotNull @NotBlank String token) {
        emailService.verifyEmail(token);
        return ReturnResultUtil.success();
    }
}

