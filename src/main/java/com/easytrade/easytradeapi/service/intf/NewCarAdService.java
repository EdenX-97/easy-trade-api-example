/*
 * @Description: 新车广告服务层类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 00:33:41
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 03:37:28
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/NewCarAdService.java
 */
package com.easytrade.easytradeapi.service.intf;

import java.util.List;

import com.easytrade.easytradeapi.constant.enums.CancelAdReasonEnum;
import com.easytrade.easytradeapi.constant.enums.GearboxTypeEnum;
import com.easytrade.easytradeapi.model.NewCarAd;
import com.easytrade.easytradeapi.model.User;
import org.bson.types.ObjectId;


public interface NewCarAdService {
    /**
     * @description: 获取所有已发布的新车广告
     * @param {*}
     * @return {List<NewCarAd>} 所有已发布的新车广告
     */    
    public List<NewCarAd> getPostedNewCarAds();

    /**
     * @description: 更新已发布广告的缓存
     * @param {*}
     * @return {*}
     */    
    public void updatePostedAdsInRedis();
    
    /**
     * @description: 创建新车广告，只有车商能创建
     * @param {NewCarAd} newCarAd 输入的新车广告变量
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public ObjectId createNewCarAd(NewCarAd newCarAd, String token);

    /**
     * @description: 发布新车广告
     * @param {String} newCarId 输入的新车广告id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */    
    public void postNewCarAd(ObjectId newCarId, String token);

    /**
     * @description: 取消新车广告
     * @param {String} newCarId 输入的新车广告id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public void cancelNewCarAd(ObjectId newCarId, String token);

    /**
     * @description: 根据广告和用户来取消目标广告
     * @param {NewCarAd} newCarAd 被删除的广告
     * @param {User} dealer 拥有该广告的车商
     * @param {CancelAdReasonEnum} calcelAdReason 取消广告的原因
     * @return {*}
     */
    public void cancelTargetAd(NewCarAd newCarAd, User dealer, CancelAdReasonEnum calcelAdReason);

    /**
     * @description: 取消超过一年的新车广告
     * @param {*}
     * @return {*}
     */
    public void cancelAdsOverOneYeaer();

    /**
     * @description:
     * @param {String} newCarId 输入的新车广告id
     * @param {long} realPrice 用户输入的真实价格
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */    
    public void completeNewCarAd(ObjectId newCarId, long realPrice, String token);

    /**
     * @description 根据车商id获取其名下的所有新车广告id
     * @param {ObjectId} userId 车商账号
     * @return {List<String>} 广告id的集合
     */
    public List<NewCarAd> getAllNewCarAdByUserID(ObjectId userId);

    /**
     * @description 根据变速箱模式获得所有新车广告
     * @param {GearboxTypeEnum} gte 变速箱模式
     * @return {List<NewCarAd>}  得到的新车广告的类的集合
     */
    public List<NewCarAd> getAllNewCarAdByGearboxTypeEnum(GearboxTypeEnum gte);

    /**
     * @description 根据车辆模版id获得所有新车广告
     * @param {String} exampleCarId 车辆模版id
     * @return {List<NewCarAd>}  得到的新车广告的类的集合
     */
    public List<NewCarAd> getAllNewCarAdByExampleCarId(ObjectId exampleCarId);

    ///**
    // * 添加新汽车广告
    // *
    // * @param model        模型
    // * @param gte          变速箱模式
    // * @param exampleCarId 汽车id
    // * @param userId       用户id
    // * @param imgURL       图片地址
    // */
    //public void addNewCarAd(String model, GearboxTypeEnum gte, ObjectId exampleCarId, ObjectId userId, String imgURL);

    /**
     * 根据新车广告id删除新汽车广告
     *
     * @param id id
     */
    public void deleteNewCarAd(ObjectId id);

    ///**
    // * 根据新车广告id更新新汽车广告
    // *
    // * @param id           id
    // * @param model        模型
    // * @param gte          一种
    // * @param exampleCarId 例如汽车id
    // * @param userId       用户id
    // */
    //public void updateNewCarAd(ObjectId id, String model, GearboxTypeEnum gte, ObjectId exampleCarId, ObjectId userId);


    /**
     * 得到所有车型
     *
     * @return {@link List}<{@link String}> 车型名称集合
     */
    public List<String> getAllModel();


    /**
     * 得到所有变速箱
     *
     * @return {@link List}<{@link GearboxTypeEnum}>
     */
    public List<GearboxTypeEnum> getAllGearbox();


    /**
     * 通过id获取一个新车广告
     *
     * @param id id 新车广告id
     * @return {@link NewCarAd}
     */
    public NewCarAd getOneById(ObjectId id);
}
