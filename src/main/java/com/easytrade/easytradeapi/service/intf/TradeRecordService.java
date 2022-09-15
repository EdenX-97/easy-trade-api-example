/**
 * @author: Hongzhang Liu
 * @description 交易记录相关的接口
 * @date 7/4/2022 5:40 pm
 */
package com.easytrade.easytradeapi.service.intf;

import com.easytrade.easytradeapi.constant.enums.TradeStatusEnum;
import com.easytrade.easytradeapi.constant.enums.TradeTypeEnum;
import com.easytrade.easytradeapi.model.TradeRecord;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.List;

public interface TradeRecordService {
    /**
     * @description: 根据交易单号找到交易记录
     * @param {String} tradeNo 交易单号
     * @return {TradeRecord} 一条交易记录
     */
    public TradeRecord getOneTradeRecordByTradeNo(String tradeNo);

    /**
     * @description: 根据交易类型获得交易记录集合
     * @param {TradeTypeEnum}tte 交易类型
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> getAllTradeRecordByType(TradeTypeEnum tte);

    /**
     * @description: 根据价格区间获得交易记录集合
     * @param {BigDecimal} price1 起始价格
     * @param {BigDecimal} price2 终止价格
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> getAllTradeRecordByPriceBetween(BigDecimal price1, BigDecimal price2);

    /**
     * @description: 根据创建日期区间获得交易记录集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> getAllTradeRecordByCreateDateBetween(String date1, String date2);

    /**
     * @description: 根据用户账号获得交易记录集合
     * @param {String} account 用户账号
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> getAllTradeRecordByOwnerAccount(String account);

    /**
     * @description: 根据广告id获得交易记录集合
     * @param {ObjectId} id 广告id
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> getAllTradeRecordByAdId(ObjectId id);

    /**
     * 添加贸易记录
     *
     * @param tradeNo      订单号
     * @param tte          交易类型
     * @param price        交易价格
     * @param createDate   交易创建日期
     * @param ownerAccount 拥有者账户
     * @param adId         对应广告id
     * @param tse          交易状态
     */
    public void addTradeRecord(String tradeNo, TradeTypeEnum tte, BigDecimal price, String createDate, String ownerAccount, ObjectId adId, TradeStatusEnum tse);

    /**
     * 根据交易记录id删除交易记录
     *
     * @param id 交易记录id
     */
    public void deleteTradeRecordById(ObjectId id);

    /**
     * 根据交易记录id更新交易记录
     *
     * @param id           交易记录id
     * @param tradeNo      订单号
     * @param tte          交易类型
     * @param price        交易价格
     * @param createDate   交易创建日期
     * @param ownerAccount 拥有者账户
     * @param adId         对应广告id
     * @param tse          交易状态
     */
    public void updateTradeRecordById(ObjectId id, String tradeNo, TradeTypeEnum tte, BigDecimal price, String createDate, String ownerAccount, ObjectId adId, TradeStatusEnum tse);
}
