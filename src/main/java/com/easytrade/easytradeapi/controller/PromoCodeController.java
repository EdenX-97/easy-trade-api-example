/*
 * @Description: 优惠码控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 03:38:06
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-12 22:16:40
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/PromoCodeController.java
 */
package com.easytrade.easytradeapi.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.UserStatusEnum;
import com.easytrade.easytradeapi.model.PromoCode;
import com.easytrade.easytradeapi.service.intf.PromoCodeService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Validated
public class PromoCodeController {
    @Autowired
    PromoCodeService promoCodeService;

    /**
     * @description: 创建优惠码
     * @param {long} freeAdNums 使用优惠码后增加的免费广告次数
     * @param {long} maxUseTimes 该优惠码的最大使用次数，-1为无限次
     * @param {long} expireDay 优惠码的过期天数
     * @param {UserStatusEnum} suitableUserStatus 优惠码适用的用户类型
     * @return {Result} 结果信息，带有创建的优惠码代码
     */    
    @PostMapping("/promoCode/create")
    @Transactional
    public Result createPromoCode(@RequestParam @NotNull long freeAdNums,
                                  @RequestParam @NotNull long maxUseTimes,
                                  @RequestParam @NotNull long expireDay,
                                  @RequestParam @NotNull UserStatusEnum suitableUserStatus) {
        String code = promoCodeService.createPromoCode(freeAdNums, maxUseTimes, expireDay, suitableUserStatus);
        return ReturnResultUtil.success(code);
    }

    /**
     * @description: 使用优惠码
     * @param {String} code 优惠码的六位代码
     * @param {String} token 从header中获取的用户登录后的token
     * @return {Result} 结果信息
     */    
    @PostMapping("/promoCode/use")
    @Transactional
    public Result usePromoCode(@RequestParam @NotNull String code,
                               @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        promoCodeService.usePromoCode(code, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 取消优惠码，该优惠码并不会被删除而是设为canceled状态
     * @param {String} code 优惠码的六位代码
     * @return {Result} 结果信息
     */    
    @PostMapping("/promoCode/cancel")
    @Transactional
    public Result cancelPromoCode(@RequestParam @NotNull String code) {
        promoCodeService.cancelPromoCode(code);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 根据免费标准广告次数获取推广码
     * @param {long} freeAdNums 免费标准广告次数
     * @return {List<PromoCode>} 推广码集合
     */
    @GetMapping("/promoCode/getAllPromoCodeByFreeAdNums")
    @Transactional
    public Result getAllPromoCodeByFreeAdNums(long freeAdNums){
        List<PromoCode> rs = promoCodeService.getAllPromoCodeByFreeAdNums(freeAdNums);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据免费标准广告次数区间获取推广码
     * @param {long} freeAdNums1 免费标准广告次数
     * @param {long} freeAdNums2 免费标准广告次数
     * @return {List<PromoCode>} 推广码集合
     */
    @GetMapping("/promoCode/getAllPromoCodeByFreeAdNumsBetween")
    @Transactional
    public Result getAllPromoCodeByFreeAdNumsBetween(long freeAdNums1,long freeAdNums2){
        List<PromoCode> rs = promoCodeService.getAllPromoCodeByFreeAdNumsBetween(freeAdNums1, freeAdNums2);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据创建日期区间获取推广码
     * @param date1 {Date} 起始日期
     * @param date2 {Date} 结束日期
     * @return {List<PromoCode>} 推广码集合
     */
    @GetMapping("/promoCode/getAllPromoCodeServiceByCreateDateBetween")
    @Transactional
    public Result getAllPromoCodeServiceByCreateDateBetween(String date1, String date2){
        List<PromoCode> rs = promoCodeService.getAllPromoCodeByCreateDateBetween(date1, date2);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据过期日期区间获取推广码
     * @param date1 {Date} 起始日期
     * @param date2 {Date} 结束日期
     * @return {List<PromoCode>} 推广码集合
     */
    @GetMapping("/promoCode/getAllPromoCodeServiceByExpireDateBetween")
    @Transactional
    public Result getAllPromoCodeServiceByExpireDateBetween(String date1, String date2){
        List<PromoCode> rs = promoCodeService.getAllPromoCodeByExpireDateBetween(date1, date2);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据使用次数区间获取推广码
     * @param {long} times1 起始值
     * @param {long} times2 结束值
     * @return {List<PromoCode>} 推广码集合
     */
    @GetMapping("/promoCode/getAllPromoCodeByUsedTimesBetween")
    @Transactional
    public Result getAllPromoCodeByUsedTimesBetween(long times1, long times2){
        List<PromoCode> rs = promoCodeService.getAllPromoCodeByUsedTimesBetween(times1, times2);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据最大使用次数区间获取推广码
     * @param {long} times1 起始值
     * @param {long} times2 结束值
     * @return {List<PromoCode>} 推广码集合
     */
    @GetMapping("/promoCode/getAllPromoCodeByMaxUsedTimesBetween")
    @Transactional
    public Result getAllPromoCodeByMaxUsedTimesBetween(long times1, long times2){
        List<PromoCode> rs = promoCodeService.getAllPromoCodeByMaxUsedTimesBetween(times1, times2);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据适用用户类型获取推广码
     * @param {String} status 用户类型
     * @return {List<PromoCode>} 推广码集合
     */
    @GetMapping("/promoCode/getAllPromoCodeBySuitableUserStatus")
    @Transactional
    public Result getAllPromoCodeBySuitableUserStatus(String status){
        List<PromoCode> rs = promoCodeService.getAllPromoCodeBySuitableUserStatus(status);
        return ReturnResultUtil.success(rs);
    }

    ///**
    // * 添加优惠码
    // *
    // * @param code               优惠码
    // * @param freeAdNums         免费标准广告使用次数
    // * @param createDate         创建日期
    // * @param expireDate         过期日期
    // * @param usedTimes          使用时间
    // * @param maxUseTimes        最大使用次数
    // * @param suitableUserStatus 合适用户类别
    // */
    //@PostMapping("/promoCode/addPromoCode")
    //public Result addPromoCode(@RequestParam @NotNull String code,
    //                           @RequestParam @NotNull long freeAdNums,
    //                           @RequestParam @NotNull String createDate,
    //                           @RequestParam @NotNull String expireDate,
    //                           @RequestParam @NotNull long usedTimes,
    //                           @RequestParam @NotNull long maxUseTimes,
    //                           @RequestParam @NotNull String suitableUserStatus){
    //    promoCodeService.addPromoCode(code, freeAdNums, createDate, expireDate, usedTimes,maxUseTimes,suitableUserStatus);
    //    return ReturnResultUtil.success("add promoCode successfully");
    //}
    //
    ///**
    // * 根据代码删除优惠码
    // *
    // * @param code 优惠码
    // */
    //@PostMapping("/promoCode/deletePromoCodeByCode")
    //public Result deletePromoCodeByCode(String code){
    //    promoCodeService.deletePromoCodeByCode(code);
    //    return ReturnResultUtil.success("delete promoCode successfully");
    //}
    //
    ///**
    // * 根据代码号更新优惠码信息
    // *
    // * @param code               代码号
    // * @param freeAdNums         免费广告次数
    // * @param createDate         创建日期
    // * @param expireDate         过期日期
    // * @param usedTimes          使用次数
    // * @param maxUseTimes        最大使用次数
    // * @param suitableUserStatus 合适用户类别
    // */
    //@PostMapping("/promoCode/updatePromoCodeByCode")
    //public Result updatePromoCodeByCode(@RequestParam @NotNull String code,
    //                                    @RequestParam @NotNull long freeAdNums,
    //                                    @RequestParam @NotNull String createDate,
    //                                    @RequestParam @NotNull String expireDate,
    //                                    @RequestParam @NotNull long usedTimes,
    //                                    @RequestParam @NotNull long maxUseTimes,
    //                                    @RequestParam @NotNull String suitableUserStatus){
    //    promoCodeService.updatePromoCodeByCode(code, freeAdNums, createDate, expireDate, usedTimes, maxUseTimes, suitableUserStatus);
    //    return ReturnResultUtil.success("update promoCode successfully");
    //}
}

