/*
 * @Description: 二手车广告服务层类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-07 19:46:33
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 03:51:05
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/SecondHandCarAdService.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/SecondHandCarAdService.java
 */
package com.easytrade.easytradeapi.service.intf;

import java.util.List;
import com.easytrade.easytradeapi.constant.enums.CancelAdReasonEnum;
import com.easytrade.easytradeapi.constant.enums.GearboxTypeEnum;
import com.easytrade.easytradeapi.model.SecondHandCarAd;
import com.easytrade.easytradeapi.model.User;
import org.bson.types.ObjectId;


public interface SecondHandCarAdService {
    /**
     * @description: 获取所有已发布的二手车广告
     * @param {*}
     * @return {List<SecondHandCarAd>} 所有已发布的二手车广告
     */
    public List<SecondHandCarAd> getPostedSecondHandCarAds();

    /**
     * @description: 更新已发布广告的缓存
     * @param {*}
     * @return {*}
     */
    public void updatePostedAdsInRedis();
    
    /**
     * @description: 创建二手车广告，车商和用户都可以调用
     * @param {SecondHandCarAd} secondHandCarAd 输入的二手车广告，映射而来
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public ObjectId createSecondHandCarAd(SecondHandCarAd secondHandCarAd, String token);

    /**
     * @description: 发布二手车广告
     * @param {String} secondHandCarId 输入的二手车广告Id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */    
    public void postSecondHandCarAd(ObjectId secondHandCarId, String token);

    /**
     * @description: 取消二手车广告
     * @param {String} secondHandCarId 输入的二手车广告Id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public void cancelSecondHandCarAd(ObjectId secondHandCarId, String token);

    /**
     * @description: 取消超过一年的二手车广告
     * @param {*}
     * @return {*}
     */
    public void cancelAdsOverOneYear();

    /**
     * @description: 内部方法，根据广告和用户来取消目标广告
     * @param {SecondHandCarAd} secondHandCarAd 被删除的广告
     * @param {User} user 拥有该广告的用户
     * @param {CancelAdReasonEnum} calcelAdReason 取消广告的原因
     * @return {*}
     */
    public void cancelTargetAd(SecondHandCarAd secondHandCarAd, User user,
            CancelAdReasonEnum calcelAdReason);

    /**
     * @description:
     * @param {String} secondHandCarId 输入的二手车广告id
     * @param {long} realPrice 用户输入的真实价格
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public void completeSecondHandCarAd(ObjectId secondHandCarId, long realPrice, String token);

    /**
     * @description 根据用户id获取其名下的所有二手车广告id
     * @param {ObjectId} userId 用户账号
     * @return {List<String>} 广告id的集合
     */
    public List<SecondHandCarAd> getAllSecondHandCarAdByUserID(ObjectId userId);

    /**
     * @description 根据购买日期区间获得所有二手车集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public List<SecondHandCarAd> getAllSecondHandCarByExampleCarId(ObjectId exampleCarId);

    /**
     * @description 根据生产日期区间获得所有二手车集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public List<SecondHandCarAd> getAllSecondHandCarByPurchaseDateBetween(String date1, String date2);

    /**
     * @description 根据大架号获得所有二手车
     * @param {String} bigFrame 大架号
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public List<SecondHandCarAd> getAllSecondHandCarByProductionDateBetween(String date1, String date2);

    /**
     * @description 根据车牌号获得二手车
     * @param {String} license 车牌号
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public SecondHandCarAd getOneSecondHandCarByBigFrame(String bigFrame);

    /**
     * @description 根据车牌号获得二手车
     * @param {String} license 车牌号
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public SecondHandCarAd getOneSecondHandCarByLicense(String license);

    /**
     * @description 根据行驶公里数区间获得所有二手车
     * @param kilo1 起始公里数
     * @param kilo2 终止公里数
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public List<SecondHandCarAd> getAllSecondHandCarByKilometersBetween(long kilo1, long kilo2);

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
    //public void addSecondHandCarAd(String model, GearboxTypeEnum gte, ObjectId exampleCarId, String purchaseDate, String productionDate, String bigFrame, String license, long kilo, ObjectId userId);

    /**
     * 删除二手汽车广告通过id
     *
     * @param id id 二手汽车广告id
     */
    public void deleteSecondHandCarAdById(ObjectId id);

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
    public void updateSecondHandCarAdById(ObjectId id, String model, GearboxTypeEnum gte, ObjectId exampleCarId, String purchaseDate, String productionDate, String bigFrame, String license, long kilo, ObjectId userId);


    /**
     * 通过id获得一个二手车广告
     *
     * @param id id 二手车广告id
     * @return {@link SecondHandCarAd}
     */
    public SecondHandCarAd getOneById(ObjectId id);
}
