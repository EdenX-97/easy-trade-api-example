/*
 * @Description: 优惠码服务层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 03:00:13
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-10 20:31:21
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/PromoCodeService.java
 */
package com.easytrade.easytradeapi.service.intf;

import com.easytrade.easytradeapi.constant.enums.UserStatusEnum;
import com.easytrade.easytradeapi.model.PromoCode;

import java.util.List;


public interface PromoCodeService {
    /**
     * @description: 创建优惠码
     * @param {long} freeAdNums 添加的免费广告次数
     * @param {long} maxUseTimes 最大使用次数限制，-1为无限次
     * @param {long} expireDay 过期时间，单位为天
     * @param {UserStatusEnum} suitableUserStatus 适用的用户类型
     * @return {String} 生成的六位优惠码
     */
    public String createPromoCode(long freeAdNums, long maxUseTimes, long expireDay,
            UserStatusEnum suitableUserStatus);

    /**
     * @description: 使用优惠码
     * @param {String} code 输入的优惠码
     * @param {String} token 用户的jwt
     * @return {*}
     */
    public void usePromoCode(String code, String token);

    /**
     * @description: 取消优惠码，逻辑上是将该优惠码的最大次数设置为0，不会从数据库中删除
     * @param {String} code 取消的优惠码
     * @return {*}
     */
    public void cancelPromoCode(String code);

    /**
     * @description: 根据免费标准广告次数获取推广码
     * @param {long} freeAdNums 免费标准广告次数
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> getAllPromoCodeByFreeAdNums(long freeAdNums);

    /**
     * @description: 根据免费标准广告次数区间获取推广码
     * @param {long} freeAdNums1 免费标准广告次数
     * @param {long} freeAdNums2 免费标准广告次数
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> getAllPromoCodeByFreeAdNumsBetween(long freeAdNums1, long freeAdNums2);

    /**
     * @description: 根据创建日期区间获取推广码
     * @param date1 {Date} 起始日期
     * @param date2 {Date} 结束日期
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> getAllPromoCodeByCreateDateBetween(String date1, String date2);

    /**
     * @description: 根据过期日期区间获取推广码
     * @param date1 {Date} 起始日期
     * @param date2 {Date} 结束日期
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> getAllPromoCodeByExpireDateBetween(String date1, String date2);

    /**
     * @description: 根据使用次数区间获取推广码
     * @param {long} times1 起始值
     * @param {long} times2 结束值
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> getAllPromoCodeByUsedTimesBetween(long times1, long times2);

    /**
     * @description: 根据最大使用次数区间获取推广码
     * @param {long} times1 起始值
     * @param {long} times2 结束值
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> getAllPromoCodeByMaxUsedTimesBetween(long times1, long times2);

    /**
     * @description: 根据适用用户类型获取推广码
     * @param {String} status 用户类型
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> getAllPromoCodeBySuitableUserStatus(String status);


    /**
     * 添加优惠码
     *
     * @param code               优惠码
     * @param freeAdNums         免费标准广告使用次数
     * @param createDate         创建日期
     * @param expireDate         过期日期
     * @param usedTimes          使用时间
     * @param maxUseTimes        最大使用次数
     * @param suitableUserStatus 合适用户类别
     */
    public void addPromoCode(String code, long freeAdNums, String createDate, String expireDate, long usedTimes, long maxUseTimes, String suitableUserStatus);

    /**
     * 根据代码删除优惠码
     *
     * @param code 优惠码
     */
    public void deletePromoCodeByCode(String code);

    /**
     * 根据代码号更新优惠码信息
     *
     * @param code               代码号
     * @param freeAdNums         免费广告次数
     * @param createDate         创建日期
     * @param expireDate         过期日期
     * @param usedTimes          使用次数
     * @param maxUseTimes        最大使用次数
     * @param suitableUserStatus 合适用户类别
     */
    public void updatePromoCodeByCode(String code, long freeAdNums, String createDate, String expireDate, long usedTimes, long maxUseTimes, String suitableUserStatus);
}

