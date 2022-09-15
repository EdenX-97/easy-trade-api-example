/**
 * @author: Hongzhang Liu
 * @description 微信支付接口类
 * @date 20/7/2022 1:45 pm
 */
package com.easytrade.easytradeapi.service.intf;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface WechatPaySerivce {


    /**
     * 创建订单
     *
     * @param id    广告id
     * @param token 令牌
     * @return {@link String} 二维码url
     * @throws IOException ioexception
     */
    public String createOrder(ObjectId id, String token) throws Exception;


    /**
     * 付款通知处理
     *
     * @param notifyData 通知数据
     */
    public String payNotify(String notifyData) throws GeneralSecurityException, IOException;
}
