/*
 * @Description:
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-09 21:26:24
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 04:28:16
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/AlipayServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.common.models.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.easytrade.easytradeapi.constant.consists.VehicleAd;
import com.easytrade.easytradeapi.constant.enums.AdLevelEnum;
import com.easytrade.easytradeapi.constant.enums.AdStatusEnum;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.enums.TradeStatusEnum;
import com.easytrade.easytradeapi.constant.enums.TradeTypeEnum;
import com.easytrade.easytradeapi.constant.enums.VehicleAdTypeEnum;
import com.easytrade.easytradeapi.constant.exceptions.PayException;
import com.easytrade.easytradeapi.model.TradeRecord;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.NewCarAdRepository;
import com.easytrade.easytradeapi.repository.SecondHandCarAdRepository;
import com.easytrade.easytradeapi.repository.TradeRecordRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.AlipayService;
import com.easytrade.easytradeapi.service.intf.VehicleAdService;
import com.easytrade.easytradeapi.utils.AlipayUtil;
import com.easytrade.easytradeapi.utils.JWTUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AlipayServiceImpl implements AlipayService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    NewCarAdRepository newCarAdRepository;

    @Autowired
    TradeRecordRepository tradeRecordRepository;

    @Autowired
    SecondHandCarAdRepository secondHandCarAdRepository;

    @Autowired
    VehicleAdService vehicleAdService;

    @Value("${alipay.returnUrl}")
    private String returnUrl;

    @Value("${alipay.tradeExpireMins}")
    private long tradeExpireMins;

    @Override
    public String payAd(ObjectId adId, String token) {
        // 检查并获取广告
        VehicleAd carAd = checkAndGetAd(adId, token);

        // 判断该广告是否处于创建阶段
        if (!carAd.getAdStatus().equals(AdStatusEnum.CREATED)) {
            throw new PayException(ResultCodeEnum.FAILED, "Cannot pay for ad not in created");
        }

        // 判断该广告是否已经有订单，如果有订单则查询状态
        List<TradeRecord> tradeRecords = tradeRecordRepository.findAllByAdId(adId);
        if (tradeRecords != null && !tradeRecords.isEmpty() ) {
            String tradeStatus = getTradeStatus(adId, token);
            switch (tradeStatus) {
                //case "WAIT_BUYER_PAY":
                //    // 订单已创建，等待付款
                //    throw new PayException(ResultCodeEnum.FAILED, "Please continue to pay");
                case "TRADE_SUCCESS":
                case "TRADE_FINISHED":
                    // 订单已完成，不可重复付款
                    throw new PayException(ResultCodeEnum.FAILED, "Trade completed");
            }
            // 如果订单为超时关闭，可以重新创建订单
        }

        // 开始进行支付
        AdLevelEnum adLevel = carAd.getAdLevel();
        //PriceLevelEnum priceLevel = carAd.getPriceLevel();
        long price = carAd.getPrice();
        // 获取广告类型
        VehicleAdTypeEnum type = carAd.getVehicleAdType();

        String adPrice = vehicleAdService.getAdPrice(adLevel, price, type) + ".00";

        String adName = null;
        Boolean ifUseFreeAd = false;
        User user = userRepository.findOneByPhone(JWTUtil.getValue(token));
        long freeAdNums = user.getFreeAdNums();
        switch (adLevel) {
            case STANDARD:
                adName = "标准广告";
                // 如果是标准广告，则自动消耗免费次数
                if (freeAdNums > 0) {
                    ifUseFreeAd = true;
                }
                break;
            case ADVANCED:
                adName = "进阶广告";
                break;
            case ELITE:
                adName = "精英广告";
                break;
        }

        // 将订单号保存至数据库，获取返回的支付宝页面
        TradeRecord savedTradeRecord = new TradeRecord();
        savedTradeRecord.setAdId(adId);
        savedTradeRecord.setCreateDate(new Date());
        //savedTradeRecord.setOwnerAccount(carAd.getOwnerAccount());
        savedTradeRecord.setType(TradeTypeEnum.ALIPAY);
        String returnPage;
        if (ifUseFreeAd) {
            String tradeNo = AlipayUtil.generateTradeNo();
            savedTradeRecord.setTradeNo(tradeNo);
            // 如果使用免费广告，则保存消耗后的广告数，并设置交易记录为使用免费广告
            user.setFreeAdNums(freeAdNums - 1);
            userRepository.save(user);
            savedTradeRecord.setStatus(TradeStatusEnum.FREE);
            returnPage = tradeNo;
        } else {
            // 不使用免费广告，则创建新的订单号，并设置交易记录为支付中，并获取支付宝界面
            String tradeNo = AlipayUtil.generateTradeNo();
            savedTradeRecord.setTradeNo(tradeNo);
            savedTradeRecord.setPrice(new BigDecimal(adPrice));
            savedTradeRecord.setStatus(TradeStatusEnum.PAYING);
            try {
                AlipayTradePagePayResponse res =
                        Factory.Payment.Page().pay(adName, tradeNo, adPrice, returnUrl);
                returnPage = res.getBody();
            } catch (Exception e) {
                throw new PayException(ResultCodeEnum.FAILED, "Ali pay fail: " + e.getMessage());
            }
        }
        tradeRecordRepository.save(savedTradeRecord);

        return returnPage;
    }

    @Override
    public String getTradeStatus(ObjectId adId, String token) {
        // 检查广告
        checkAndGetAd(adId, token);

        // 检查并获取订单
        List<TradeRecord> tradeRecords = tradeRecordRepository.findAllByAdId(adId);
        // 根据日期排序
        tradeRecords.sort(Comparator.comparing(TradeRecord::getCreateDate));
        // 如果订单大于1个，使用最新的
        TradeRecord tradeRecord = tradeRecords.get(tradeRecords.size()-1);

        if (tradeRecord.getStatus() == TradeStatusEnum.CLOSED) {
            return TradeStatusEnum.CLOSED.toString();
        }

        if (tradeRecord.getStatus() == TradeStatusEnum.FREE) {
            return TradeStatusEnum.FREE.toString();
        }

        String tradeNo = tradeRecord.getTradeNo();

        // 检查订单号格式
        if (!AlipayUtil.validateTradeNo(tradeNo)) {
            throw new PayException(ResultCodeEnum.INVALID_PARAM, "Trade number incorrect");
        }

        // 获取订单状态
        AlipayTradeQueryResponse res;
        try {
            res = Factory.Payment.Common().query(tradeNo);
        } catch (Exception e) {
            throw new PayException(ResultCodeEnum.FAILED, "Ali query fail: " + e.getMessage());
        }
        String tradeStatus = res.getTradeStatus();

        if (tradeStatus == null) {
            //throw new PayException(ResultCodeEnum.FAILED, "Trade not created");
            tradeStatus = "NOT_CREATED";
        }

        return tradeStatus;
    }

    @Override
    public void validateTrade(String tradeNo, String token) {
        // 校验交易单号合法性
        if (!AlipayUtil.validateTradeNo(tradeNo)) {
            throw new PayException(ResultCodeEnum.FAILED, "Trade number format wrong");
        }

        // 检查并获取广告
        if (!tradeRecordRepository.existsByTradeNo(tradeNo)) {
            throw new PayException(ResultCodeEnum.FAILED, "Trade not exist");
        }
        TradeRecord tradeRecord = tradeRecordRepository.findOneByTradeNo(tradeNo);
        VehicleAd vehicleAd = vehicleAdService.getVehicleById(tradeRecord.getAdId());

        // 获取对应Repository
        MongoRepository repository = getRepository(vehicleAd.getVehicleAdType());

        // 判断该广告是否处于创建阶段
        if (!vehicleAd.getAdStatus().equals(AdStatusEnum.CREATED)) {
            throw new PayException(ResultCodeEnum.FAILED, "Cannot pay for ad not in created");
        }

        // 如果订单为支付中，则检查订单状态，如果是免费广告，则直接保存
        TradeStatusEnum status = tradeRecord.getStatus();
        switch (status) {
            case PAYING:
                // 获取订单状态
                String tradeStatus = getTradeStatus(vehicleAd.getId(), token);

                // 如果订单没有完成支付则报错
                if (!tradeStatus.equals("TRADE_SUCCESS")) {
                    throw new PayException(ResultCodeEnum.FAILED, "Payment not complete");
                }

                // 设置订单为已完成并保存至数据库
                tradeRecord.setStatus(TradeStatusEnum.PAID);
                tradeRecordRepository.save(tradeRecord);
            case FREE:
                // 设置订单为已支付并保存至数据库
                vehicleAd.setAdStatus(AdStatusEnum.PAID);
                repository.save(vehicleAd);
                break;
            default:
                throw new PayException(ResultCodeEnum.FAILED, "Cannot validate the trade");
        }
    }

    @Override
    public void refundAd(ObjectId adId, String token) {
        // 检查广告
        checkAndGetAd(adId, token);

        // 获取订单记录，只有已支付的订单才能退款
        TradeRecord tradeRecord = tradeRecordRepository.findOneByAdId(adId);
        if (tradeRecord == null) {
            throw new PayException(ResultCodeEnum.NOT_FOUND, "Trade record not found");
        }
        if (!tradeRecord.getStatus().equals(TradeStatusEnum.PAID)) {
            throw new PayException(ResultCodeEnum.FAILED, "Trade not paid");
        }
        if (tradeRecord.getStatus().equals(TradeStatusEnum.FREE)) {
            throw new PayException(ResultCodeEnum.FAILED, "Cannot refund free ad");
        }

        // 获取订单状态
        String tradeStatus = getTradeStatus(adId, token);

        // 如果订单没有完成支付则报错
        if (!tradeStatus.equals("TRADE_SUCCESS")) {
            throw new PayException(ResultCodeEnum.FAILED, "Payment not complete");
        }

        // 开始退款
        try {
            Factory.Payment.Common().refund(tradeRecord.getTradeNo(), tradeRecord.getPrice().toString());
        } catch (Exception e) {
            throw new PayException(ResultCodeEnum.FAILED, "Cannot refund: " + e.getMessage());
        }

        // 将订单记录设置为已退款，保存至数据库
        tradeRecord.setStatus(TradeStatusEnum.REFUNDED);
        tradeRecordRepository.save(tradeRecord);
    }

    @Override
    public Boolean checkRefundSuccess(ObjectId adId, String token) {
        // 检查广告
        checkAndGetAd(adId, token);

        TradeRecord tradeRecord = tradeRecordRepository.findOneByAdId(adId);
        if (tradeRecord == null) {
            throw new PayException(ResultCodeEnum.NOT_FOUND, "Trade record not found");
        }

        Boolean refundSuccess = null;
        try {
            String tradeNo = tradeRecord.getTradeNo();
            AlipayTradeFastpayRefundQueryResponse res =
                    Factory.Payment.Common().queryRefund(tradeNo, tradeNo);
            //System.out.println(tradeNo);
            //System.out.println(res.gmtRefundPay);
            String refundStatus = res.getRefundStatus();
            if (refundStatus.equals("REFUND_SUCCESS")) {
                refundSuccess = true;
            } else {
                refundSuccess = false;
            }
        } catch (Exception e) {
            throw new PayException(ResultCodeEnum.FAILED,
                    "Cannot get refund status: " + e.getMessage());
        }

        return refundSuccess;
    }

    @Override
    public void cancelTradesOverTime() {
        // 取出所有支付中的交易记录
        List<TradeRecord> tradeRecords =
                tradeRecordRepository.findAllByStatus(TradeStatusEnum.PAYING);
        if (tradeRecords == null) {
            return;
        }

        // 如果该交易记录超时，则对其进行关闭
        Date nowDate = new Date();
        for (TradeRecord tradeRecord : tradeRecords) {
            Date createDate = tradeRecord.getCreateDate();
            long durationMinutes =
                    Duration.between(createDate.toInstant(), nowDate.toInstant()).toMinutes();
            if (durationMinutes > tradeExpireMins) {
                try {
                    Factory.Payment.Common().close(tradeRecord.getTradeNo());
                } catch (Exception e) {
                    throw new PayException(ResultCodeEnum.FAILED,
                            "Ali cloase fail: " + e.getMessage());
                }

                tradeRecord.setStatus(TradeStatusEnum.CLOSED);
                tradeRecordRepository.save(tradeRecord);
            }
        }

        log.info("Complete auto cancel trades over time");
    }

    /**
     * @description: 内部方法，检查并获取广告
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @param {MongoRepository} repository 广告的持久层
     * @return {VehicleAd} 返回的广告
     */
    private VehicleAd checkAndGetAd(ObjectId adId, String token) {
        // 检查输入的用户是否存在
        String phone = JWTUtil.getValue(token);
        if (!userRepository.existsByPhone(phone)) {
            throw new PayException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }

        // 检查id对应载具是否存在并获取
        VehicleAd vehicleAd = vehicleAdService.getVehicleById(adId);

        // 判断用户是否拥有该广告
        if (!vehicleAd.getUserId().equals(userRepository.findOneByPhone(phone).getId())) {
            throw new PayException(ResultCodeEnum.FAILED, "User is not this ad owner");
        }

        return vehicleAd;
    }

    /**
     * @description: 内部方法，根据载具类型获取对应的Repository
     * @param {VehicleAdTypeEnum} type 载具类型
     * @return {MongoRepository} 返回一个Repository，用于获取具体的model信息
     */
    private MongoRepository getRepository(VehicleAdTypeEnum type) {
        switch (type) {
            case NEWCARAD:
                return newCarAdRepository;
            case SECONDHANDCARAD:
                return secondHandCarAdRepository;
        }
        return null;
    }
}
