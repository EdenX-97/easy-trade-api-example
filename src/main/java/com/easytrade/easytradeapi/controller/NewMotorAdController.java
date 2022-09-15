/**
 * @author: Hongzhang Liu
 * @description 新摩托控制器
 * @date 4/7/2022 5:39 pm
 */
package com.easytrade.easytradeapi.controller;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.model.NewMotorAd;
import com.easytrade.easytradeapi.service.intf.NewMotorAdService;
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
public class NewMotorAdController {

    @Autowired
    NewMotorAdService newMotorAdService;

    // 每10分钟的定时
    private final static String updatePostedAdsInRedisSchedule = "0 */1 * ? * ?";

    /**
     * @description: 创建新摩托广告
     * @param {NewCarAd} newCarAd 输入的二手摩托车广告变量
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    @PostMapping("/motor/newMotor/createNewMotorAd")
    @Transactional
    public Result createNewMotorAd(@RequestBody @NotNull NewMotorAd newMotorAd,
                                          @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(newMotorAdService.createNewMotorAd(newMotorAd, token));
    }

    /**
     * @description:
     * @param {String} newCarId 输入的新摩托车广告id
     * @param {long} realPrice 用户输入的真实价格
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    @PostMapping("/motor/newMotor/completeNewMotorAd")
    @Transactional
    public Result completeNewMotorAd(@RequestParam @NotNull ObjectId id,
                                   @RequestParam @NotNull long realPrice,
                                   @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        newMotorAdService.completeNewMotorAd(id, realPrice, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 取消新摩托车广告
     * @param {String} newCarId 输入的新摩托车广告id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/motor/newMotor/cancelNewMotorAd")
    @Transactional
    public Result cancelNewMotorAd(@RequestParam @NotNull ObjectId id,
                                 @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        newMotorAdService.cancelNewMotorAd(id, token);
        return ReturnResultUtil.success();
    }

//    /**
//     * @description: 更新缓存中的已发布广告
//     * @param {*}
//     * @return {Result} 结果信息
//     */
//    @PostMapping("/car/newMotor/updatePostedAdsInRedis")
//    @Scheduled(cron = updatePostedAdsInRedisSchedule)
//    public Result updatePostedAdsInRedis() {
//        newMotorAdService.updatePostedAdsInRedis();
//        return ReturnResultUtil.success();
//    }
}
