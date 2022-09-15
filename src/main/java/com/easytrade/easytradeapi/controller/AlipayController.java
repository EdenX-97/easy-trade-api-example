/*
 * @Description: 支付宝服务控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-08 00:58:36
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 02:05:46
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/AlipayController.java
 */
package com.easytrade.easytradeapi.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.VehicleAdTypeEnum;
import com.easytrade.easytradeapi.service.intf.AlipayService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class AlipayController {
    @Autowired
    AlipayService alipayService;

    // 每半个小时的定时
    private final static String cancelTradesOverTimeSchedule = "0 */30 * ? * ?";

    /**
     * @description: 支付广告费用
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息，带有返回的页面String
     */
    @PostMapping("/alipay/pay")
    @Transactional
    public Result alipay(@RequestParam @NotNull ObjectId adId,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(alipayService.payAd(adId, token));
    }

    /**
     * @description: 获取订单状态
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息，包含订单状态
     */
    @GetMapping("/alipay/getTradeStatus")
    public Result getTradeStatus(@RequestParam @NotNull ObjectId adId,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(alipayService.getTradeStatus(adId, token));
    }

    /**
     * @description: 检查订单完成
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/alipay/validateTrade")
    @Transactional
    public Result validateTrade(@RequestParam @NotNull String tradeNo,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        alipayService.validateTrade(tradeNo, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 订单退款
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/alipay/refundAd")
    @Transactional
    public Result refundAd(@RequestParam @NotNull ObjectId adId,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        alipayService.refundAd(adId, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 查询退款状态
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息，包含退款是否成功信息
     */
    @GetMapping("/alipay/checkRefundSuccess")
    public Result checkRefundSuccess(@RequestParam @NotNull ObjectId adId,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(alipayService.checkRefundSuccess(adId, token));
    }

    /**
     * @description: 取消所有超时的订单
     * @param {*}
     * @return {Result} 返回的结果信息
     */
    @PostMapping("/alipay/cancelTradesOverTime")
    @Scheduled(cron = cancelTradesOverTimeSchedule)
    @Transactional
    public Result cancelTradesOverTime() {
        alipayService.cancelTradesOverTime();
        return ReturnResultUtil.success();
    }
}

