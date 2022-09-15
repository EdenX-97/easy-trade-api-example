/*
 * @Description: 新车广告位的控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 00:30:41
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-05-27 03:34:57
 * 
 * @FilePath: /undefined/Users/xumo/Entrepreneurship/easyTradeAPI/EasyTradeApi/src/main/java/com/easytrade/easytradeapi/controller/NewCarAdController.java
 */
package com.easytrade.easytradeapi.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.GearboxTypeEnum;
import com.easytrade.easytradeapi.model.NewCarAd;
import com.easytrade.easytradeapi.service.intf.NewCarAdService;
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
public class NewCarAdController {
    @Autowired
    private NewCarAdService newCarAdService;

    // 每周日凌晨3.10的定时
    private static final String cancelAdsOverOneYeaerSchedule = "0 10 3 ? * SUN";

    // 每10分钟的定时
    private final static String updatePostedAdsInRedisSchedule = "0 */10 * ? * ?";

    /**
     * @description: 获取所有已发布广告，优先从redis获取缓存
     * @param {*}
     * @return {Result} 结果信息，包含所有已发布广告
     */    
    @GetMapping("/car/newCar/getPostedNewCarAds")
    @Cacheable(cacheNames = "posted:newCar")
    public Result getPostedNewCarAds() {
        return ReturnResultUtil.success(newCarAdService.getPostedNewCarAds());
    }

    /**
     * @description: 更新缓存中的已发布广告
     * @param {*}
     * @return {Result} 结果信息
     */    
    @PostMapping("/car/newCar/updatePostedAdsInRedis")
    @Scheduled(cron = updatePostedAdsInRedisSchedule)
    public Result updatePostedAdsInRedis() {
        newCarAdService.updatePostedAdsInRedis();
        return ReturnResultUtil.success();
    }

    /**
     * @description: 创建新车广告，只有车商能创建
     * @param {NewCarAd} newCarAd 输入的新车广告变量
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/car/newCar/createNewCarAd")
    @Transactional
    public Result createNewCarAd(@RequestBody @NotNull NewCarAd newCarAd,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(newCarAdService.createNewCarAd(newCarAd, token));
    }

    /**
     * @description: 发布新车广告
     * @param {String} newCarId 输入的新车广告id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */    
    @PostMapping("/car/newCar/postNewCarAd")
    @Transactional
    public Result postNewCarAd(@RequestParam @NotNull ObjectId newCarId,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        newCarAdService.postNewCarAd(newCarId, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 取消新车广告
     * @param {String} newCarId 输入的新车广告id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/car/newCar/cancelNewCarAd")
    @Transactional
    public Result cancelNewCarAd(@RequestParam @NotNull ObjectId id,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        newCarAdService.cancelNewCarAd(id, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 取消超过一年的新车广告，固定时间自动执行
     * @param {*}
     * @return {Result} 结果信息
     */
    @PostMapping("/car/newCar/cancelAdsOverOneYeaer")
    @Scheduled(cron = cancelAdsOverOneYeaerSchedule)
    @Transactional
    public Result cancelAdsOverOneYeaer() {
        newCarAdService.cancelAdsOverOneYeaer();
        return ReturnResultUtil.success();
    }

    /**
     * @description: 完成新车广告
     * @param {String} id 输入的新车广告id
     * @param {long} realPrice 卖家输入的真实价格
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {Result} 结果信息
     */
    @PostMapping("/car/newCar/completeNewCarAd")
    @Transactional
    public Result completeNewCarAd(@RequestParam @NotNull ObjectId id,
            @RequestParam @NotNull long realPrice,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        newCarAdService.completeNewCarAd(id, realPrice, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 根据车商id获取到其对应的新车广告类的集合
     * @param {ObjectId} userId 车商用户id
     * @return {Result} 结果信息
     */
    @GetMapping("/car/newcar/getAllNewCarAdByUserId")
    @Transactional
    public Result getAllNewCarAdByUserId(@RequestParam @NotNull ObjectId userId){
        List<NewCarAd> rs = newCarAdService.getAllNewCarAdByUserID(userId);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据变速箱模式获取新车广告类的集合
     * @param {GearboxTypeEnum} gte 变速箱模式
     * @return {Result} 结果信息
     */
    @GetMapping("/car/newcar/getAllNewCarAdByGearbox")
    @Transactional
    public Result getAllNewCarAdByGearbox(@RequestParam @NotNull GearboxTypeEnum gte){
        List<NewCarAd> rs = newCarAdService.getAllNewCarAdByGearboxTypeEnum(gte);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据样本车辆id获取新车广告类的集合
     * @param {String} exampleCarId 样本车辆id
     * @return {Result} 结果信息
     */
    @GetMapping("/car/newcar/getAllNewCarAdByExampleCarId")
    @Transactional
    public Result getAllNewCarAdByExampleCarId(@RequestParam @NotNull ObjectId exampleCarId){
        List<NewCarAd> rs = newCarAdService.getAllNewCarAdByExampleCarId(exampleCarId);
        return ReturnResultUtil.success(rs);
    }

    ///**
    // * 添加新汽车广告
    // *
    // * @param model        模型
    // * @param gte          变速箱模式
    // * @param exampleCarId 汽车id
    // * @param userId       用户id
    // * @param imgURL       图片地址
    // */
    //@PostMapping("/car/newcar/addNewCarAd")
    //@Transactional
    //public Result addNewCarAd(@RequestParam @NotNull String model,
    //                          @RequestParam @NotNull GearboxTypeEnum gte,
    //                          @RequestParam @NotNull ObjectId exampleCarId,
    //                          @RequestParam @NotNull ObjectId userId,
    //                          @RequestParam @NotNull String imgURL){
    //    newCarAdService.addNewCarAd(model, gte, exampleCarId, userId, imgURL);
    //    return ReturnResultUtil.success("add new car successfully");
    //}

    /**
     * 根据新车广告id删除新汽车广告
     *
     * @param id id
     */
    @PostMapping("/car/newcar/deleteNewCarAd")
    @Transactional
    public Result deleteNewCarAd(@RequestParam @NotNull ObjectId id){
        newCarAdService.deleteNewCarAd(id);
        return ReturnResultUtil.success("delete new car successfully");
    }

    ///**
    // * 根据新车广告id更新新汽车广告
    // *
    // * @param id           id
    // * @param model        模型
    // * @param gte          一种
    // * @param exampleCarId 例如汽车id
    // * @param userId       用户id
    // */
    //@PostMapping("/car/newcar/updateNewCarAd")
    //@Transactional
    //public Result updateNewCarAd(@RequestParam @NotNull ObjectId id,
    //                             @RequestParam @NotNull String model,
    //                             @RequestParam @NotNull GearboxTypeEnum gte,
    //                             @RequestParam @NotNull ObjectId exampleCarId,
    //                             @RequestParam @NotNull ObjectId userId){
    //    newCarAdService.updateNewCarAd(id, model, gte, exampleCarId, userId);
    //    return ReturnResultUtil.success("update new car successfully");
    //}

    /**
     * 得到所有车型
     *
     * @return {@link List}<{@link String}> 车型名称集合
     */
    @GetMapping("/car/newcar/getAllModel")
    @Transactional
//    @Cacheable(cacheNames = "newcar:models")
    public Result getAllModel(){
        return ReturnResultUtil.success(newCarAdService.getAllModel());
    }

    /**
     * 根据id获得新车广告
     *
     * @param id id
     * @return {@link NewCarAd}
     */
    @GetMapping("/car/newcar/getOneById")
    @Transactional
    public Result getOneByIdAndAdStatus(@RequestParam @NotNull ObjectId id){
        return ReturnResultUtil.success(newCarAdService.getOneById(id));
    }
}

