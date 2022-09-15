/*
 * @Description: 短信控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-05 17:44:26
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-06 01:21:49
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/SMSController.java
 */
package com.easytrade.easytradeapi.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.service.intf.ShortMessageService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class SMSController {
    @Autowired
    ShortMessageService shortMessageService;

    /**
     * @description: 发送验证短信
     * @param {String} phone 接收短信的手机号
     * @return {Result} 结果信息
     */
    @PostMapping("/sms/sendVerifyMessage")
    public Result sendVerifyMessage(@RequestParam @NotNull @NotBlank String phone) {
        shortMessageService.sendVerifyMessage(phone);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 验证手机号
     * @param {String} phone 验证的手机号
     * @param {String} code 验证码
     * @return {Result} 结果信息
     */
    @PostMapping("/sms/verifyPhone")
    public Result verifyPhone(@RequestParam @NotNull @NotBlank String phone,
            @RequestParam @NotNull @NotBlank String code) {
        shortMessageService.verifyPhone(phone, code);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 获取已发送的验证码状态信息
     * @param {String} phone 用户电话
     * @param {String} date 查询的日期，格式为yyyyMMdd，如：20220101
     * @return {Result} 包含返回的状态信息，如：发送成功，接收时间为:2022-01-05 17:42:53
     */
    @GetMapping("/sms/getVerifyCodeStatus")
    public Result getVerifyCodeStatus(@RequestParam @NotNull @NotBlank String phone,
            @RequestParam @NotNull @NotBlank String date) {
        return ReturnResultUtil.success(shortMessageService.getVerifyCodeStatus(phone, date));
    }
}

