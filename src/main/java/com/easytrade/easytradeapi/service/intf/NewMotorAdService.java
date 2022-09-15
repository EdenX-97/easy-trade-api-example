/**
 * @author: Hongzhang Liu
 * @description 新摩托服务接口
 * @date 4/7/2022 5:06 pm
 */
package com.easytrade.easytradeapi.service.intf;

import com.easytrade.easytradeapi.constant.enums.CancelAdReasonEnum;
import com.easytrade.easytradeapi.model.NewMotorAd;
import com.easytrade.easytradeapi.model.User;
import org.bson.types.ObjectId;

public interface NewMotorAdService {
    /**
     * @description: 创建新摩托广告，只有车商能创建
     * @param {NewCarAd} newCarAd 输入的新车广告变量
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public ObjectId createNewMotorAd(NewMotorAd newMotorAd, String token);

    /**
     * @description:
     * @param {String} newCarId 输入的新摩托车广告id
     * @param {long} realPrice 用户输入的真实价格
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public void completeNewMotorAd(ObjectId newMotorId, long realPrice, String token);

    /**
     * @description: 取消新摩托车广告
     * @param {String} newCarId 输入的新摩托车广告id
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @return {*}
     */
    public void cancelNewMotorAd(ObjectId newMotorId, String token);

    /**
     * 取消目标广告
     *
     * @param newMotorAd     新摩托车广告
     * @param dealer         经销商
     * @param cancelAdReason 取消广告原因
     */
    public void cancelTargetAd(NewMotorAd newMotorAd, User dealer, CancelAdReasonEnum cancelAdReason);

//    /**
//     * @description: 更新已发布广告的缓存
//     * @param {*}
//     * @return {*}
//     */
//    public void updatePostedAdsInRedis();
}
