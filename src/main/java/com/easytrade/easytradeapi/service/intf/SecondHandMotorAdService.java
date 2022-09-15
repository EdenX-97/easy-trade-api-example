/**
 * @author: Hongzhang Liu
 * @description 二手摩托服务接口
 * @date 4/7/2022 5:06 pm
 */
package com.easytrade.easytradeapi.service.intf;

import com.easytrade.easytradeapi.constant.enums.CancelAdReasonEnum;
import com.easytrade.easytradeapi.model.SecondHandMotorAd;
import com.easytrade.easytradeapi.model.User;
import org.bson.types.ObjectId;

public interface SecondHandMotorAdService {
    /**
     * @description: 创建二手摩托广告
     * @param {NewCarAd} newCarAd 输入的二手摩托车广告变量
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public ObjectId createSecondHandMotorAd(SecondHandMotorAd secondHandMotorAd, String token);

    /**
     * @description:
     * @param {String} secondHandCarId 输入的二手摩托车广告id
     * @param {long} realPrice 用户输入的真实价格
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public void completeSecondHandMotorAd(ObjectId secondHandMotorId, long realPrice, String token);

    /**
     * @description: 内部方法，根据广告和用户来取消目标广告
     * @param {SecondHandCarAd} secondHandCarAd 被删除的广告
     * @param {User} user 拥有该广告的用户
     * @param {CancelAdReasonEnum} calcelAdReason 取消广告的原因
     * @return {*}
     */
    public void cancelTargetAd(SecondHandMotorAd secondHandMotorAd, User user,
                               CancelAdReasonEnum cancelAdReason);

    /**
     * @description: 取消二手摩托车广告
     * @param {String} secondHandCarId 输入的二手摩托车广告Id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public void cancelSecondHandMotorAd(ObjectId secondHandMotorId, String token);
}
