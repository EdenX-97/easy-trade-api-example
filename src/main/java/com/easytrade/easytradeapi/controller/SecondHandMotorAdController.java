/**
 * @author: Hongzhang Liu
 * @description 二手摩托控制器
 * @date 4/7/2022 5:39 pm
 */
package com.easytrade.easytradeapi.controller;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.model.SecondHandMotorAd;
import com.easytrade.easytradeapi.service.intf.SecondHandMotorAdService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@Validated
public class SecondHandMotorAdController {

    @Autowired
    SecondHandMotorAdService secondHandMotorAdService;

    /**
     * @description: 创建二手摩托广告
     * @param {NewCarAd} newCarAd 输入的二手摩托车广告变量
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    @PostMapping("/motor/secondHandMotor/createSecondHandMotorAd")
    @Transactional
    public Result createSecondHandMotorAd(@RequestBody @NotNull SecondHandMotorAd secondHandMotorAd,
                                          @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(secondHandMotorAdService.createSecondHandMotorAd(secondHandMotorAd, token));
    }

    /**
     * @description: 完成二手摩托车广告
     * @param {String} id 输入的二手摩托车广告id
     * @param {long} realPrice 卖家输入的真实价格
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @Transactional
    @PostMapping("/motor/secondHandMotor/completeSecondHandMotorAd")
    public Result completeSecondHandMotorAd(@RequestParam @NotNull ObjectId id,
                                          @RequestParam @NotNull long realPrice,
                                          @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        secondHandMotorAdService.completeSecondHandMotorAd(id, realPrice, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 取消二手摩托车广告
     * @param {String} secondHandCarId 输入的二手摩托车广告id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/motor/secondHandMotor/cancelSecondHandMotorAd")
    @Transactional
    public Result cancelSecondHandCarAd(@RequestParam @NotNull ObjectId id,
                                        @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        secondHandMotorAdService.cancelSecondHandMotorAd(id, token);
        return ReturnResultUtil.success();
    }
}
