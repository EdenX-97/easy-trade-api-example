/*
 * @Description: 支付宝服务层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-09 00:45:37
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 01:48:33
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/AlipayService.java
 */
package com.easytrade.easytradeapi.service.intf;

import com.easytrade.easytradeapi.constant.enums.VehicleAdTypeEnum;
import org.bson.types.ObjectId;


public interface AlipayService {
    /**
     * @description: 支付广告费用
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {String} 返回的页面String
     */    
    public String payAd(ObjectId adId, String token);

    /**
     * @description: 查询订单状态
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {String} 返回订单状态
     */
    public String getTradeStatus(ObjectId adId, String token);

    /**
     * @description: 检查订单状态
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */    
    public void validateTrade(String tradeNo, String token);

    /**
     * @description: 订单退款
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public void refundAd(ObjectId adId, String token);

    /**
     * @description: 查询退款是否成功
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {String} 退款状态
     */    
    public Boolean checkRefundSuccess(ObjectId adId, String token);

    /**
     * @description: 取消所有超时的订单
     * @param {*}
     * @return {*}
     */    
    public void cancelTradesOverTime();
}
