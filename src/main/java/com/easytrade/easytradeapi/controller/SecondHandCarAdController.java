/*
 * @Description: 二手车广告位的控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 00:31:19
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 03:56:02
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/SecondHandCarAdController.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/SecondHandCarAdController.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/SecondHandCarAdController.java
 */
package com.easytrade.easytradeapi.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.GearboxTypeEnum;
import com.easytrade.easytradeapi.model.SecondHandCarAd;
import com.easytrade.easytradeapi.service.intf.SecondHandCarAdService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
public class SecondHandCarAdController {
    @Autowired
    SecondHandCarAdService secondHandCarAdService;

    // 每周日凌晨3.00的定时
    private static final String cancelAdsOverOneYearSchedule = "0 0 3 ? * SUN";

    // 每10分钟的定时
    private final static String updatePostedAdsInRedisSchedule = "0 */10 * ? * ?";

    /**
     * @description: 获取所有已发布广告，优先从redis获取缓存
     * @param {*}
     * @return {Result} 结果信息，包含所有已发布广告
     */
    @GetMapping("/car/secondHandCar/getPostedSecondHandCarAds")
    @Cacheable(value = "posted:secondHandCar")
    public Result getPostedSecondHandCarAds() {
        return ReturnResultUtil.success(secondHandCarAdService.getPostedSecondHandCarAds());
    }

    /**
     * @description: 更新缓存中的已发布广告
     * @param {*}
     * @return {Result} 结果信息
     */
    @PostMapping("/car/secondHandCar/updatePostedAdsInRedis")
    @Scheduled(cron = updatePostedAdsInRedisSchedule)
    @Transactional
    public Result updatePostedAdsInRedis() {
        secondHandCarAdService.updatePostedAdsInRedis();
        return ReturnResultUtil.success();
    }

    /**
     * @description: 创建二手车广告，只有车商能创建
     * @param {SecondHandCarAd} secondHandCarAd 输入的二手车广告变量
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/car/secondHandCar/createSecondHandCarAd")
    @Transactional
    public Result createSecondHandCarAd(
            @RequestBody @Validated @NotNull SecondHandCarAd secondHandCarAd,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(secondHandCarAdService.createSecondHandCarAd(secondHandCarAd, token));
    }

    /**
     * @description: 发布二手车广告
     * @param {String} secondHandCarId 输入的二手车广告id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/car/secondHandCar/postSecondHandCarAd")
    @Transactional
    public Result postSecondHandCarAd(@RequestParam @NotNull ObjectId secondHandCarId,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        secondHandCarAdService.postSecondHandCarAd(secondHandCarId, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 取消二手车广告
     * @param {String} secondHandCarId 输入的二手车广告id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/car/secondHandCar/cancelSecondHandCarAd")
    @Transactional
    public Result cancelSecondHandCarAd(@RequestParam @NotNull ObjectId id,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        secondHandCarAdService.cancelSecondHandCarAd(id, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 取消超过一年的二手车广告，固定时间自动执行
     * @param {*}
     * @return {Result} 结果信息
     */
    @PostMapping("/car/secondHandCar/cancelAdsOverOneYear")
    @Scheduled(cron = cancelAdsOverOneYearSchedule)
    @Transactional
    public Result cancelAdsOverOneYear() {
        secondHandCarAdService.cancelAdsOverOneYear();
        return ReturnResultUtil.success();
    }

    /**
     * @description: 完成二手车广告
     * @param {String} id 输入的二手车广告id
     * @param {long} realPrice 卖家输入的真实价格
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @Transactional
    @PostMapping("/car/secondHandCar/completeSecondHandCarAd")
    public Result completeSecondHandCarAd(@RequestParam @NotNull ObjectId id,
            @RequestParam @NotNull long realPrice,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        secondHandCarAdService.completeSecondHandCarAd(id, realPrice, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 根据用户id获取到其对应的二手车广告类的集合
     * @param userId 用户id
     * @return {Result} 结果信息
     */
    @GetMapping("/car/secondHandCar/getAllSecondHandCarAdByUserId")
    @Transactional
    public Result getAllSecondHandCarAdByUserID(@RequestParam @NotNull ObjectId userId){
        List<SecondHandCarAd> rs = secondHandCarAdService.getAllSecondHandCarAdByUserID(userId);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description 根据样本车辆id获得所有二手车集合
     * @param {String} exampleCarId 样本车辆id
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    @GetMapping("/car/secondHandCar/getAllSecondHandCarByExampleCarId")
    @Transactional
    public Result getAllSecondHandCarByExampleCarId(@RequestParam @NotNull ObjectId exampleCarId){
        List<SecondHandCarAd> rs = secondHandCarAdService.getAllSecondHandCarByExampleCarId(exampleCarId);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description 根据购买日期区间获得所有二手车集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    @GetMapping("/car/secondHandCar/getAllSecondHandCarByPurchaseDateBetween")
    @Transactional
    public Result getAllSecondHandCarByPurchaseDateBetween(@RequestParam @NotNull String date1, String date2){
        List<SecondHandCarAd> rs = secondHandCarAdService.getAllSecondHandCarByPurchaseDateBetween(date1, date2);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description 根据生产日期区间获得所有二手车集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    @GetMapping("/car/secondHandCar/getAllSecondHandCarByProductionDateBetween")
    @Transactional
    public Result getAllSecondHandCarByProductionDateBetween(@RequestParam @NotNull String date1, @RequestParam @NotNull String date2){
        List<SecondHandCarAd> rs = secondHandCarAdService.getAllSecondHandCarByProductionDateBetween(date1, date2);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description 根据大架号获得所有二手车
     * @param {String} bigFrame 大架号
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    @GetMapping("/car/secondHandCar/getOneSecondHandCarByBigFrame")
    @Transactional
    public Result getOneSecondHandCarByBigFrame(@RequestParam @NotNull String bigFrame){
        SecondHandCarAd rs = secondHandCarAdService.getOneSecondHandCarByBigFrame(bigFrame);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description 根据车牌号获得二手车
     * @param {String} license 车牌号
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    @GetMapping("/car/secondHandCar/getOneSecondHandCarByLicense")
    @Transactional
    public Result getOneSecondHandCarByLicense(@RequestParam @NotNull String license){
        SecondHandCarAd rs = secondHandCarAdService.getOneSecondHandCarByLicense(license);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description 根据行驶公里数区间获得所有二手车
     * @param kilo1 起始公里数
     * @param kilo2 终止公里数
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    @GetMapping("/car/secondHandCar/getAllSecondHandCarByKilometersBetween")
    @Transactional
    public Result getAllSecondHandCarByKilometersBetween(@RequestParam @NotNull long kilo1, long kilo2){
        List<SecondHandCarAd> rs = secondHandCarAdService.getAllSecondHandCarByKilometersBetween(kilo1, kilo2);
        return ReturnResultUtil.success(rs);
    }

    ///**
    // * 添加二手汽车广告
    // *
    // * @param model          模型
    // * @param gte            变速箱模式 (手动/自动)
    // * @param exampleCarId   样本汽车id
    // * @param purchaseDate   购买日期
    // * @param productionDate 生产日期
    // * @param bigFrame       大架号
    // * @param license        牌照号
    // * @param kilo           公里数
    // * @param userId         所属用户id
    // */
    //@PostMapping("/car/secondHandCar/addSecondHandCarAd")
    //@Transactional
    //public Result addSecondHandCarAd(@RequestParam @NotNull String model,
    //                                 @RequestParam @NotNull GearboxTypeEnum gte,
    //                                 @RequestParam @NotNull ObjectId exampleCarId,
    //                                 @RequestParam @NotNull String purchaseDate,
    //                                 @RequestParam @NotNull String productionDate,
    //                                 @RequestParam @NotNull String bigFrame,
    //                                 @RequestParam @NotNull String license,
    //                                 @RequestParam @NotNull long kilo,
    //                                 @RequestParam @NotNull ObjectId userId){
    //    secondHandCarAdService.addSecondHandCarAd(model, gte, exampleCarId, purchaseDate, productionDate, bigFrame, license, kilo, userId);
    //    return ReturnResultUtil.success("add second hand car ad successfully");
    //}

    /**
     * 删除二手汽车广告通过id
     *
     * @param id id 二手汽车广告id
     */
    @PostMapping("/car/secondHandCar/deleteSecondHandCarAdById")
    @Transactional
    public Result deleteSecondHandCarAdById(@RequestParam @NotNull ObjectId id){
        secondHandCarAdService.deleteSecondHandCarAdById(id);
        return ReturnResultUtil.success("delete second hand car ad successfully");
    }

    /**
     * 更新二手汽车广告通过id
     *
     * @param id             id
     * @param model          模型
     * @param gte            变速箱模式 (手动/自动)
     * @param exampleCarId   样本汽车id
     * @param purchaseDate   购买日期
     * @param productionDate 生产日期
     * @param bigFrame       大架号
     * @param license        牌照号
     * @param kilo           公里数
     * @param userId         所属用户id
     */
    @PostMapping("/car/secondHandCar/updateSecondHandCarAdById")
    @Transactional
    public Result updateSecondHandCarAdById(@RequestParam @NotNull ObjectId id,
                                            @RequestParam @NotNull String model,
                                            @RequestParam @NotNull GearboxTypeEnum gte,
                                            @RequestParam @NotNull ObjectId exampleCarId,
                                            @RequestParam @NotNull String purchaseDate,
                                            @RequestParam @NotNull String productionDate,
                                            @RequestParam @NotNull String bigFrame,
                                            @RequestParam @NotNull String license,
                                            @RequestParam @NotNull long kilo,
                                            @RequestParam @NotNull ObjectId userId){
        secondHandCarAdService.updateSecondHandCarAdById(id, model, gte, exampleCarId, purchaseDate, productionDate, bigFrame, license, kilo, userId);
        return ReturnResultUtil.success("update second hand car ad successfully");
    }

    /**
     * 通过id获得一个二手车广告
     *
     * @param id id 二手车广告id
     * @return {@link SecondHandCarAd}
     */
    @GetMapping("/car/secondHandCar/getOneById")
    @Transactional
    public Result getOneById(@RequestParam @NotNull ObjectId id){
        return ReturnResultUtil.success(secondHandCarAdService.getOneById(id));
    }
}
